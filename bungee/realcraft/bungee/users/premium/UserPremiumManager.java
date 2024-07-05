package realcraft.bungee.users.premium;

import realcraft.share.users.User;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

public class UserPremiumManager {

    public UserPremiumManager() {
        new UserPremiumSuggester();
        new UserPremiumCommand();
    }

    public static CompletableFuture<Boolean> checkPossiblePremium(User user) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();

        CompletableFuture.supplyAsync(() -> {
            try {
                HttpURLConnection request = (HttpURLConnection) new URL("https://api.mojang.com/users/profiles/minecraft/" + user.getName()).openConnection();
                request.setConnectTimeout(5000);
                request.setReadTimeout(5000);
                request.setDoOutput(true);
                String line;
                StringBuilder output = new StringBuilder();
                BufferedReader in = new BufferedReader(new InputStreamReader(request.getInputStream()));
                while((line = in.readLine()) != null) output.append(line);
                in.close();
                return output.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }).whenComplete((result, throwable) -> {
            future.complete(result.charAt(0) == '{' && result.length() > 10);
        });

        return future;
    }
}
