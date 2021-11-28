package com.github.zhuaidadaya.modMdo.commands;

public class DimensionTips {
    public String getDimensionColor(String dimension) {
        String result;
        switch(dimension) {
            case "overworld" -> result = "§a";
            case "the_nether" -> result = "§e";
            case "the_end" -> result = "§c";
            default -> result = "";
        }
        return result;
    }

    public String getDimensionName(String dimension) {
        String result;
        switch(dimension) {
            case "overworld" -> result = "主世界";
            case "the_nether" -> result = "下界";
            case "the_end" -> result = "末地";
            default -> result = "";
        }
        return result;
    }

    public XYZ overworldXyzToNetherXyz(XYZ overworld) {
        overworld.divideXZ(8, 8);

        return overworld;
    }
    /*
    case "pos-nether-to-overworld" -> {
        String[] split = arr.split(", ");

        for (int i = 0; i < 3; i++) {
            try {
                if (!(i == 1))
                    split[i] = String.valueOf(Double.parseDouble(split[i].substring(0, split[i].indexOf(".") + 3).replace("d", "")) * 8);
                else
                    split[i] = split[i].substring(0, split[i].indexOf(".") + 3).replace("d", "");
            } catch (Exception e) {

            }
        }

        sorting = Arrays.toString(split);

        return sorting;
    }
            case "pos-overworld-to-nether" -> {
        String[] split = arr.split(", ");

        for (int i = 0; i < 3; i++) {
            try {
                if (!(i == 1))
                    split[i] = String.valueOf(Double.parseDouble(split[i].substring(0, split[i].indexOf(".") + 3).replace("d", "")) / 8);
                else
                    split[i] = split[i].substring(0, split[i].indexOf(".") + 3).replace("d", "");
            } catch (Exception e) {

            }
        }

        sorting = Arrays.toString(split);

        return sorting;
    }*/
}
