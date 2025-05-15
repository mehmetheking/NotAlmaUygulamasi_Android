package com.mehmetbirolgolge.notuygulamasi.helpers;

import java.util.Random;

public class AIHelper {

    // Not başlığı oluştur (offline)
    public static String generateTitle(String content) {
        if (content == null || content.isEmpty()) {
            return "Yeni Not";
        }

        // İçeriğin kısa bir özetini çıkarır
        String[] sentences = content.split("[.!?]");
        if (sentences.length > 0) {
            String firstSentence = sentences[0].trim();
            if (firstSentence.length() > 30) {
                return firstSentence.substring(0, 30) + "...";
            } else {
                return firstSentence;
            }
        }

        return "Yeni Not";
    }

    // Not içeriğini düzenle (offline)
    public static String processContent(String content) {
        if (content == null || content.isEmpty()) {
            return "";
        }

        // Markdown formatı uygula
        StringBuilder result = new StringBuilder();
        result.append("# Not Özeti\n\n");

        // Not içeriğini cümlelere böl
        String[] sentences = content.split("[.!?]");

        // Madde işaretli liste oluştur
        for (String sentence : sentences) {
            String trimmed = sentence.trim();
            if (!trimmed.isEmpty()) {
                result.append("- ").append(trimmed).append("\n");
            }
        }

        // Random bilgi notu ekle
        String[] tips = {
                "Önemli bilgileri vurgulamak için **kalın** metin kullanabilirsiniz.",
                "Listeler bilgileri düzenlemenin iyi bir yoludur.",
                "Notlarınızı düzenli tutmak hatırlamayı kolaylaştırır.",
                "Çalışırken düzenli molalar vermek verimliliği artırır."
        };

        result.append("\n## İpucu\n\n");
        result.append(tips[new Random().nextInt(tips.length)]);

        return result.toString();
    }
}