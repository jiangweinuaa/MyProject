package com.report.service;

import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * 图像特征提取服务
 * 使用颜色直方图提取图像特征向量（简单方案，不需要下载模型）
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
            
            // 提取颜色直方图特征（RGB 各 256 维，共 768 维）
            float[] features = new float[768];
            
            int width = image.getWidth();
            int height = image.getHeight();
            int[] histograms = new int[768];
            
            // 统计每个像素的 RGB 值
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int rgb = image.getRGB(x, y);
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
            
            System.out.println("✅ 颜色直方图特征提取成功，维度：" + features.length);
            return features;
            
        } catch (IOException e) {
            System.err.println("❌ 特征提取失败：" + e.getMessage());
            e.printStackTrace();
            return null;
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
