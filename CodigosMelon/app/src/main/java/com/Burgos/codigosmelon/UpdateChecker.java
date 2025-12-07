package com.Burgos.codigosmelon;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class UpdateChecker {

    public static void checkForUpdate(Context context) {
        String currentVersion = "";
        try {
            currentVersion = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (Exception e) {
            currentVersion = "";
        }

        final String finalCurrentVersion = currentVersion;

        new Thread(() -> {
            HttpsURLConnection conn = null;
            BufferedReader in = null;

            try {
                URL url = new URL("https://api.github.com/repos/marcheloBM/CodigosMelon/releases/latest");
                conn = (HttpsURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);

                in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                JSONObject json = new JSONObject(response.toString());
                String latestVersion = json.optString("tag_name", "");
                String changelog = json.optString("body", "Sin notas de versión");

                JSONArray assets = json.optJSONArray("assets");
                String downloadUrl = null;
                if (assets != null && assets.length() > 0) {
                    JSONObject asset = assets.getJSONObject(0);
                    downloadUrl = asset.optString("browser_download_url", null);
                }

                // Logs para depuración
                Log.d("UpdateChecker", "Versión actual: " + finalCurrentVersion);
                Log.d("UpdateChecker", "Última versión en GitHub: " + latestVersion);
                //Log.d("UpdateChecker", "Download URL: " + downloadUrl);

                boolean hayUpdate = (downloadUrl != null)
                        && (!latestVersion.isEmpty())
                        && (compararVersiones(finalCurrentVersion, latestVersion) < 0);

                if (hayUpdate && context instanceof Activity) {
                    final String finalLatestVersion = latestVersion;
                    final String finalChangelog = changelog;
                    final String finalDownloadUrl = downloadUrl;

                    ((Activity) context).runOnUiThread(() -> {
                        showUpdateDialog(context, finalLatestVersion, finalChangelog, finalDownloadUrl);
                    });
                }

            } catch (Exception e) {
                Log.e("UpdateChecker", "Error en checkForUpdate", e);
            } finally {
                try {
                    if (in != null) in.close();
                } catch (Exception ignored) {}
                if (conn != null) conn.disconnect();
            }
        }).start();
    }

    private static void showUpdateDialog(Context context, String latestVersion, String changelog, String downloadUrl) {
        new AlertDialog.Builder(context)
                .setTitle("Nueva versión disponible: " + latestVersion)
                .setMessage("Notas de la versión:\n\n" + changelog)
                .setPositiveButton("Descargar", (dialog, which) -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl));
                    context.startActivity(intent);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // Comparador de versiones
    private static int compararVersiones(String actual, String ultima) {
        actual = actual.replace("v", "");
        ultima = ultima.replace("v", "");

        String[] aParts = actual.split("\\.");
        String[] uParts = ultima.split("\\.");

        int length = Math.max(aParts.length, uParts.length);
        for (int i = 0; i < length; i++) {
            int a = i < aParts.length ? safeParse(aParts[i]) : 0;
            int u = i < uParts.length ? safeParse(uParts[i]) : 0;
            if (a < u) return -1; // GitHub es más nuevo
            if (a > u) return 1;  // tu app es más nueva
        }
        return 0; // iguales
    }

    private static int safeParse(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return 0;
        }
    }
}