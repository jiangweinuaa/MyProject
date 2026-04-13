package com.report.service;

import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * 图像特征提取服务
 * 使用颜色直方图提取图像特征向量
 * 支持商品区域提取（边缘检测 + 轮廓提取）
 */
@Service
public class ImageFeatureExtractor {
    
    /**
     * 提取图像特征向量
     * @param imageData 图片二进制数据
     * @return 768 维特征向量（RGB 各 256 维直方图）
     */
    public float[] extractFeatures(byte[] imageData) {
        try {
            // 读取图片
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
            
            if (image == null) {
                System.err.println("❌ 无法读取图片");
                return null;
            }
            
            // 【方案 B】尝试提取商品 ROI 区域
            BufferedImage roiImage = extractProductROI(image);
            
            // 如果 ROI 提取失败，使用原图
            if (roiImage == null) {
                roiImage = image;
                System.out.println("⚠️ ROI 提取失败，使用整图");
            }
            
            // 【关键】统一缩放到固定尺寸（1024x1024），消除尺寸影响，保留更多细节
            BufferedImage resizedImage = resizeImage(roiImage, 1024, 1024);
            
            // 提取颜色直方图特征（RGB 各 256 维，共 768 维）
            float[] features = new float[768];
            
            int width = resizedImage.getWidth();
            int height = resizedImage.getHeight();
            int[] histograms = new int[768];
            
            // 统计每个像素的 RGB 值
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int rgb = resizedImage.getRGB(x, y);
                    int r = (rgb >> 16) & 0xFF;
                    int g = (rgb >> 8) & 0xFF;
                    int b = rgb & 0xFF;
                    
                    histograms[r]++;
                    histograms[256 + g]++;
                    histograms[512 + b]++;
                }
            }
            
            // 归一化
            int totalPixels = width * height;
            for (int i = 0; i < 768; i++) {
                features[i] = (float) histograms[i] / totalPixels;
            }
            
            System.out.println("✅ 颜色直方图特征提取成功，维度：" + features.length + 
                ", 原始尺寸=" + roiImage.getWidth() + "x" + roiImage.getHeight() +
                ", 缩放后=" + width + "x" + height);
            return features;
            
        } catch (IOException e) {
            System.err.println("❌ 特征提取失败：" + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 提取商品 ROI 区域（边缘检测 + 轮廓提取）
     * @param image 原始图片
     * @return 裁剪后的商品区域图片，失败返回 null
     */
    private BufferedImage extractProductROI(BufferedImage image) {
        try {
            int width = image.getWidth();
            int height = image.getHeight();
            
            // 1. 转灰度图
            BufferedImage grayImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int rgb = image.getRGB(x, y);
                    int r = (rgb >> 16) & 0xFF;
                    int g = (rgb >> 8) & 0xFF;
                    int b = rgb & 0xFF;
                    // 灰度公式：0.299R + 0.587G + 0.114B
                    int gray = (int)(0.299 * r + 0.587 * g + 0.114 * b);
                    grayImage.setRGB(x, y, (gray << 16) | (gray << 8) | gray);
                }
            }
            
            // 2. 高斯模糊去噪（3x3 简单模糊）
            BufferedImage blurredImage = gaussianBlur(grayImage, 3);
            
            // 3. Canny 边缘检测
            boolean[][] edges = cannyEdgeDetection(blurredImage);
            
            // 4. 找最大轮廓（商品边界）
            Rectangle productRect = findLargestContour(edges, width, height);
            
            if (productRect == null) {
                return null;
            }
            
            // 5. 裁剪商品区域（添加 5% 边距）
            int marginX = (int)(productRect.width * 0.05);
            int marginY = (int)(productRect.height * 0.05);
            int cropX = Math.max(0, productRect.x - marginX);
            int cropY = Math.max(0, productRect.y - marginY);
            int cropW = Math.min(width - cropX, productRect.width + 2 * marginX);
            int cropH = Math.min(height - cropY, productRect.height + 2 * marginY);
            
            // 确保裁剪区域有效
            if (cropW < 50 || cropH < 50) {
                return null;
            }
            
            return image.getSubimage(cropX, cropY, cropW, cropH);
            
        } catch (Exception e) {
            System.err.println("⚠️ ROI 提取失败：" + e.getMessage());
            return null;
        }
    }
    
    /**
     * 缩放图片到固定尺寸（双线性插值）
     */
    private BufferedImage resizeImage(BufferedImage image, int targetWidth, int targetHeight) {
        BufferedImage resized = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        
        double xRatio = (double)image.getWidth() / targetWidth;
        double yRatio = (double)image.getHeight() / targetHeight;
        
        for (int y = 0; y < targetHeight; y++) {
            for (int x = 0; x < targetWidth; x++) {
                int srcX = (int)((x + 0.5) * xRatio);
                int srcY = (int)((y + 0.5) * yRatio);
                srcX = Math.min(srcX, image.getWidth() - 1);
                srcY = Math.min(srcY, image.getHeight() - 1);
                resized.setRGB(x, y, image.getRGB(srcX, srcY));
            }
        }
        
        return resized;
    }
    
    /**
     * 简单的高斯模糊
     */
    private BufferedImage gaussianBlur(BufferedImage image, int kernelSize) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        
        int radius = kernelSize / 2;
        for (int y = radius; y < height - radius; y++) {
            for (int x = radius; x < width - radius; x++) {
                int sum = 0;
                int count = 0;
                for (int ky = -radius; ky <= radius; ky++) {
                    for (int kx = -radius; kx <= radius; kx++) {
                        int pixel = image.getRGB(x + kx, y + ky) & 0xFF;
                        sum += pixel;
                        count++;
                    }
                }
                int avg = sum / count;
                result.setRGB(x, y, (avg << 16) | (avg << 8) | avg);
            }
        }
        return result;
    }
    
    /**
     * Canny 边缘检测（简化版）
     */
    private boolean[][] cannyEdgeDetection(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        boolean[][] edges = new boolean[width][height];
        
        // Sobel 算子
        int[] gxKernel = {-1, 0, 1, -2, 0, 2, -1, 0, 1};
        int[] gyKernel = {-1, -2, -1, 0, 0, 0, 1, 2, 1};
        
        for (int y = 1; y < height - 1; y++) {
            for (int x = 1; x < width - 1; x++) {
                int gx = 0;
                int gy = 0;
                int k = 0;
                
                for (int ky = -1; ky <= 1; ky++) {
                    for (int kx = -1; kx <= 1; kx++) {
                        int pixel = image.getRGB(x + kx, y + ky) & 0xFF;
                        gx += pixel * gxKernel[k];
                        gy += pixel * gyKernel[k];
                        k++;
                    }
                }
                
                // 梯度幅值
                int gradient = (int)Math.sqrt(gx * gx + gy * gy);
                
                // 阈值（可调）
                edges[x][y] = gradient > 50;
            }
        }
        
        return edges;
    }
    
    /**
     * 找最大轮廓（商品边界）
     */
    private Rectangle findLargestContour(boolean[][] edges, int width, int height) {
        // 简单方法：找边缘点的最小包围盒
        int minX = width, maxX = 0;
        int minY = height, maxY = 0;
        boolean found = false;
        
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (edges[x][y]) {
                    minX = Math.min(minX, x);
                    maxX = Math.max(maxX, x);
                    minY = Math.min(minY, y);
                    maxY = Math.max(maxY, y);
                    found = true;
                }
            }
        }
        
        if (!found) {
            return null;
        }
        
        int rectWidth = maxX - minX + 1;
        int rectHeight = maxY - minY + 1;
        
        // 如果包围盒太小，说明没有明显边缘
        if (rectWidth < width * 0.3 || rectHeight < height * 0.3) {
            return null;
        }
        
        return new Rectangle(minX, minY, rectWidth, rectHeight);
    }
    
    /**
     * 矩形区域
     */
    private static class Rectangle {
        int x, y, width, height;
        
        Rectangle(int x, int y, int width, int height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
    }
    
    /**
     * 计算两个特征向量的余弦相似度
     * @param features1 特征向量 1
     * @param features2 特征向量 2
     * @return 余弦相似度（0-1，越大越相似）
     */
    public double cosineSimilarity(float[] features1, float[] features2) {
        if (features1 == null || features2 == null || 
            features1.length != features2.length) {
            return 0.0;
        }
        
        float dotProduct = 0.0f;
        float norm1 = 0.0f;
        float norm2 = 0.0f;
        
        for (int i = 0; i < features1.length; i++) {
            dotProduct += features1[i] * features2[i];
            norm1 += features1[i] * features1[i];
            norm2 += features2[i] * features2[i];
        }
        
        if (norm1 == 0.0f || norm2 == 0.0f) {
            return 0.0;
        }
        
        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
}
