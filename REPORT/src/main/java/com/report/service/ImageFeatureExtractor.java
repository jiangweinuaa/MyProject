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
            
            // 【配置化】是否启用显著性检测 ROI 提取
            boolean enableROI = true;  // 默认启用
            
            // 【方案 B】尝试提取商品 ROI 区域
            BufferedImage roiImage = null;
            if (enableROI) {
                roiImage = extractProductROI(image);
            }
            
            // 如果 ROI 提取失败，使用原图
            if (roiImage == null) {
                roiImage = image;
                if (enableROI) {
                    System.out.println("⚠️ ROI 提取失败，使用整图");
                }
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
     * 提取商品 ROI 区域（显著性检测）
     * 找到图片中最突出的区域（最不像背景的部分）
     * @param image 原始图片
     * @return 裁剪后的商品区域图片，失败返回 null
     */
    private BufferedImage extractProductROI(BufferedImage image) {
        try {
            int width = image.getWidth();
            int height = image.getHeight();
            
            // 1. 转灰度图
            int[] grayPixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int rgb = image.getRGB(x, y);
                    int r = (rgb >> 16) & 0xFF;
                    int g = (rgb >> 8) & 0xFF;
                    int b = rgb & 0xFF;
                    // 灰度公式：0.299R + 0.587G + 0.114B
                    int gray = (int)(0.299 * r + 0.587 * g + 0.114 * b);
                    grayPixels[y * width + x] = gray;
                }
            }
            
            // 2. 计算显著性图（对比度 + 边缘密度）
            float[] saliencyMap = computeSaliencyMap(grayPixels, width, height);
            
            // 3. 找到显著性最高的区域
            Rectangle productRect = findSalientRegion(saliencyMap, width, height);
            
            if (productRect == null) {
                return null;
            }
            
            // 4. 裁剪商品区域（添加 10% 边距）
            int marginX = (int)(productRect.width * 0.10);
            int marginY = (int)(productRect.height * 0.10);
            int cropX = Math.max(0, productRect.x - marginX);
            int cropY = Math.max(0, productRect.y - marginY);
            int cropW = Math.min(width - cropX, productRect.width + 2 * marginX);
            int cropH = Math.min(height - cropY, productRect.height + 2 * marginY);
            
            // 确保裁剪区域有效
            if (cropW < 50 || cropH < 50) {
                return null;
            }
            
            System.out.println("✅ ROI 提取成功：显著性区域=" + cropX + "," + cropY + "," + cropW + "x" + cropH);
            return image.getSubimage(cropX, cropY, cropW, cropH);
            
        } catch (Exception e) {
            System.err.println("⚠️ ROI 提取失败：" + e.getMessage());
            return null;
        }
    }
    
    /**
     * 计算显著性图（对比度 + 边缘密度）
     * @param grayPixels 灰度像素数组
     * @param width 图片宽度
     * @param height 图片高度
     * @return 显著性值数组（0-1，越大越显著）
     */
    private float[] computeSaliencyMap(int[] grayPixels, int width, int height) {
        float[] saliency = new float[width * height];
        
        // 计算全局平均灰度
        float globalAvg = 0;
        for (int pixel : grayPixels) {
            globalAvg += pixel;
        }
        globalAvg /= grayPixels.length;
        
        // 对每个像素计算显著性
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int idx = y * width + x;
                int centerGray = grayPixels[idx];
                
                // 1. 对比度显著性（与全局平均的差异）
                float contrastSaliency = Math.abs(centerGray - globalAvg) / 255.0f;
                
                // 2. 边缘密度显著性（与周围像素的差异）
                float edgeSaliency = 0;
                int count = 0;
                int radius = 3;
                
                for (int dy = -radius; dy <= radius; dy++) {
                    for (int dx = -radius; dx <= radius; dx++) {
                        if (dx == 0 && dy == 0) continue;
                        
                        int nx = x + dx;
                        int ny = y + dy;
                        
                        if (nx >= 0 && nx < width && ny >= 0 && ny < height) {
                            int neighborGray = grayPixels[ny * width + nx];
                            edgeSaliency += Math.abs(centerGray - neighborGray);
                            count++;
                        }
                    }
                }
                
                if (count > 0) {
                    edgeSaliency /= count;
                    edgeSaliency /= 255.0f;  // 归一化到 0-1
                }
                
                // 3. 综合显著性（对比度 + 边缘）
                saliency[idx] = 0.6f * contrastSaliency + 0.4f * edgeSaliency;
            }
        }
        
        // 归一化到 0-1
        float maxSaliency = 0;
        for (float s : saliency) {
            maxSaliency = Math.max(maxSaliency, s);
        }
        
        if (maxSaliency > 0) {
            for (int i = 0; i < saliency.length; i++) {
                saliency[i] /= maxSaliency;
            }
        }
        
        return saliency;
    }
    
    /**
     * 找到显著性最高的区域
     * @param saliencyMap 显著性图
     * @param width 图片宽度
     * @param height 图片高度
     * @return 显著性区域的包围盒
     */
    private Rectangle findSalientRegion(float[] saliencyMap, int width, int height) {
        // 1. 二值化显著性图（阈值 0.5）
        boolean[][] significant = new boolean[width][height];
        float threshold = 0.5f;
        
        int significantCount = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                float s = saliencyMap[y * width + x];
                if (s > threshold) {
                    significant[x][y] = true;
                    significantCount++;
                }
            }
        }
        
        // 如果显著性像素太少，降低阈值
        if (significantCount < width * height * 0.05) {
            threshold = 0.3f;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    float s = saliencyMap[y * width + x];
                    if (s > threshold) {
                        significant[x][y] = true;
                    }
                }
            }
        }
        
        // 2. 找最大连通区域（简单方法：找所有显著性像素的包围盒）
        int minX = width, maxX = 0;
        int minY = height, maxY = 0;
        boolean found = false;
        
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (significant[x][y]) {
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
        
        // 如果包围盒太小，说明没有明显显著区域
        if (rectWidth < width * 0.2 || rectHeight < height * 0.2) {
            return null;
        }
        
        return new Rectangle(minX, minY, rectWidth, rectHeight);
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
