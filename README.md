---
title: image-processor
tags: vailter,使用手册
---
[toc!]
### 概述
 image-processor是基于java语言实现的简单的图片处理工具。
 使用简单, API采用易用的管道流设计。
 工程很小，且不依赖第三方Jar文件。
 
  支持多种图片效果处理,如:
  
  - 图片压缩 -支持各种类型的图片压缩,可设置压缩比率
  - 等比(也支持不等比)缩放 - 支持缩小及放大
  - 旋转效果 -支持任意角度的旋转
  - 像素化 -马赛克效果,你懂得
  - 灰度处理
  - 去色处理 -黑白效果
  - 图片剪切 - 支持矩形剪切和圆形剪切(圆形剪切后为Png透明效果)
  - 老照片效果 -泛黄的照片效果
  - 对 色调,对比度,亮度 控制

集成Base64编码，实现Base64字符串和图片之间互转，方便在做移动端API使用

### 使用教程
读取图片(API提供多种形式读取图片,演示使用文件读取)：
```java
File file = new File("test.png");
ImageKit kit = ImageKit.read(file);
```

处理图片：
```java
kit.cropCircle(); // 剪切圆形图片
```

保存剪切后的图片(API支持多种形式写入，演示使用文件写入)：
```java
kit.transferTo("result.png"); 
```

Ok，演示结束啦……

怎么样是不是很简单

### JDK要求
jdk 1.6+

### Version
1.0.0

**Free Software, Yeah!**
