package com.masterthesis.metricscollector.utils;

public class Utils {

    public static Double parseMetrics(String service, String[] lines, String metric){

        Double metricUsage = 0.0;

        try {
            for (String line : lines) {
                if (line.startsWith(metric)) {
                    String[] parts = line.split("\\s+");
                    if (parts.length > 1) {
                        metricUsage = Double.parseDouble(parts[1]);
                        System.out.println(service + " " + metric +  " Usage: " + metricUsage);
                    }
                }
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.err.println("Error parsing " + metric + " usage metric: " + e.getMessage());
        }

        return metricUsage;

    }

}
