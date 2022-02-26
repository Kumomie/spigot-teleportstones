package kumomi.teleportstones.util;

public class NameUtil {
    public String argsToName(String[] args) {
        return argsToName(args, 0);
    }

    public String argsToName(String[] args, int i) {

        if (args == null || args.length <= i) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        builder.append(args[i]);

        for (int j = i + 1; j < args.length; j++) {
            builder.append(" ");
            builder.append(args[j]);
        }

        return builder.toString();

    }
}
