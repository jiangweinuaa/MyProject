package com.report.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * 图像特征提取服务
 * 支持多种特征提取算法：
 * - HISTOGRAM: 颜色直方图（768 维）
 * - HISTOGRAM_GRID: 空间划分直方图（6912 维，3x3 网格）
 * - RESNET50: 深度学习特征（512 维，待实现）
 */
@Service
public class ImageFeatureExtractor {
    
    @Autowired(required = false)
    private RecognitionConfigService configService;
    
    /**
     * 提取图像特征向量（根据配置选择算法）
     * @param imageData 图片二进制数据
     * @return 特征向量（维度取决于算法）
     */
    public float[] extractFeatures(byte[] imageData) {
        try {
            // 读取图片
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageData));
            
            if (image == null) {
                System.err.println("❌ 无法读取图片");
                return null;
            }
            
            // 【配置化】读取特征提取算法
            String algorithm = configService != null ? 
                configService.getConfig("FEATURE_ALGORITHM", "HISTOGRAM") : "HISTOGRAM";
            
            // 提取 ROI 区域
            BufferedImage roiImage = extractProductROI(image);
            if (roiImage == null) {
                roiImage = image;
            }
            
            // 根据算法选择特征提取方式
            String actualAlgorithm;  // 实际使用的算法
            float[] features;
            
            switch (algorithm) {
                case "HISTOGRAM_GRID":
                    features = extractHistogramGrid(roiImage, 3);  // 3x3 网格
                    actualAlgorithm = "HISTOGRAM_GRID";
                    break;
                case "RESNET50":
                    features = extractResNet50Features(roiImage);  // 深度学习（待实现）
                    if (features == null) {
                        // ResNet50 未实现，降级到 HISTOGRAM
                        features = extractHistogram(roiImage);
                        actualAlgorithm = "HISTOGRAM";
                        System.out.println("⚠️ 已降级使用 HISTOGRAM 算法");
                    } else {
                        actualAlgorithm = "RESNET50";
                    }
                    break;
                case "HISTOGRAM":
                default:
                    features = extractHistogram(roiImage);  // 默认颜色直方图
                    actualAlgorithm = "HISTOGRAM";
                    break;
            }
            
            System.out.println("✅ 特征提取成功，算法=" + actualAlgorithm + ", 维度=" + features.length);
            return features;
            
        } catch (IOException e) {
            System.err.println("❌ 特征提取失败：" + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 提取颜色直方图特征（RGB 各 256 维，共 768 维）
     * @param image 图片
     * @return 768 维特征向量
     */
    private float[] extractHistogram(BufferedImage image) {
        // 【关键】统一缩放到固定尺寸（1024x1024），消除尺寸影响
        BufferedImage resizedImage = resizeImage(image, 1024, 1024);
        
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
        
        return features;
    }
    
    /**
     * 提取空间划分直方图特征（3x3 网格，共 6912 维）
     * @param image 图片
     * @param gridSize 网格大小（3 表示 3x3）
     * @return 6912 维特征向量（9×768）
     */
    private float[] extractHistogramGrid(BufferedImage image, int gridSize) {
        // 统一缩放到 1024x1024
        BufferedImage resizedImage = resizeImage(image, 1024, 1024);
        
        int width = resizedImage.getWidth();
        int height = resizedImage.getHeight();
        int gridW = width / gridSize;
        int gridH = height / gridSize;
        
        // 每个网格 768 维，总共 gridSize×gridSize×768 维
        float[] features = new float[gridSize * gridSize * 768];
        
        for (int row = 0; row < gridSize; row++) {
            for (int col = 0; col < gridSize; col++) {
                // 提取每个网格的直方图
                int[] gridHist = new int[768];
                
                int startX = col * gridW;
                int startY = row * gridH;
                int endX = (col == gridSize - 1) ? width : (col + 1) * gridW;
                int endY = (row == gridSize - 1) ? height : (row + 1) * gridH;
                
                // 统计网格内的 RGB 直方图
                for (int y = startY; y < endY; y++) {
                    for (int x = startX; x < endX; x++) {
                        int rgb = resizedImage.getRGB(x, y);
                        int r = (rgb >> 16) & 0xFF;
                        int g = (rgb >> 8) & 0xFF;
                        int b = rgb & 0xFF;
                        
                        gridHist[r]++;
                        gridHist[256 + g]++;
                        gridHist[512 + b]++;
                    }
                }
                
                // 归一化并拼接到总特征向量
                int gridPixels = (endX - startX) * (endY - startY);
                int featureOffset = (row * gridSize + col) * 768;
                
                for (int i = 0; i < 768; i++) {
                    features[featureOffset + i] = (float) gridHist[i] / gridPixels;
                }
            }
        }
        
        return features;
    }
    
    /**
     * 提取 ResNet50 深度学习特征（512 维）
     * @param image 图片
     * @return 512 维特征向量（未实现时返回 null）
     */
    private float[] extractResNet50Features(BufferedImage image) {
        // TODO: 待实现深度学习特征提取
        // 需要集成 DJL (Deep Java Library)
        // 1. 加载 ResNet50 预训练模型
        // 2. 图片预处理（224x224，归一化）
        // 3. 模型推理
        // 4. 提取全连接层前的特征（512 维）
        
        System.out.println("⚠️ ResNet50 特征提取暂未实现，将降级使用颜色直方图");
        return null;  // 返回 null，由调用方处理降级
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
