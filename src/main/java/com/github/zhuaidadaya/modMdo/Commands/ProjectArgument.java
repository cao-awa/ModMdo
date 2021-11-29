package com.github.zhuaidadaya.modMdo.Commands;

import net.minecraft.util.Formatting;

public class ProjectArgument {
    public static int getOperationId(String operation) {
        if("list".equalsIgnoreCase(operation)) {
            return 0;
        } else if("start".equalsIgnoreCase(operation)) {
            return 1;
        } else if("finish".equalsIgnoreCase(operation)) {
            return 2;
        } else {
            if(operation.startsWith("sidebar.team.")) {
                String string = operation.substring("sidebar.team.".length());
                Formatting formatting = Formatting.byName(string);
                if(formatting != null && formatting.getColorIndex() >= 0) {
                    return formatting.getColorIndex() + 3;
                }
            }

            return - 1;
        }
    }

    public static String getDisplayOperationName(int operationID) {
        return switch(operationID) {
            case 0 -> "list";
            case 1 -> "start";
            case 2 -> "finish";
            case 3 -> "abandon";
            case 4 -> "remove";
            case 5 -> "member";
            default -> null;
        };
    }

    public static String[] getDisplayOperationNames() {
        String[] displayOperationNames = new String[5];

        for(int i = 0; i < 5; ++ i) {
            displayOperationNames[i] = getDisplayOperationName(i);
        }

        return displayOperationNames;
    }
}
