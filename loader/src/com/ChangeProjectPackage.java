package com;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.*;
import java.util.regex.Pattern;

public class ChangeProjectPackage {

    static Pattern SPECIAL_REGEX_CHARS = Pattern.compile("[{}()\\[\\].+*?^$\\\\|]");

    static String escapeSpecialRegexChars(String str) {
        return SPECIAL_REGEX_CHARS.matcher(str).replaceAll("\\\\$0");
    }

    public static void replaceInFiles(File dir, String from, String to, String extension) {
        File[] a = dir.listFiles();
        for (File f : a) {
            if (f.isDirectory()) {
                replaceInFiles(f, from, to, extension);
            } else if (f.getName().endsWith(extension)) {
                replaceInFile(f, from, to);
            }
        }
    }

    static byte[] fixBug(byte[] bytes) {
        for (int i = 0; i < bytes.length; i++) {
            if (bytes[i] == -48 && bytes[i + 1] == 63) {
                bytes[i] = (byte) 208;
                bytes[i + 1] = (byte) 152;
            }
        }
        return bytes;
    }

    private static void replaceInFile(File f, String from, String to) {
        try {
            Path filePath = Paths.get(f.getAbsolutePath());
            String content = new String(Files.readAllBytes(filePath));
            content = content.replaceAll(from, escapeSpecialRegexChars(to));
            Files.write(filePath, fixBug(content.getBytes()));
            System.out.println("good replace " + f.getName() + " " + from + " " + to);
        } catch (Exception e) {
            System.out.println("error replace in file" + f.getAbsolutePath());
        }
    }


    public static void main(final String[] args) {

        final String APP_ID = args[0];
        final String TEMP_DIR = args[1];

        new AppRequest(APP_ID,
                new AppRequest.Listener() {
                    @Override
                    public void run(AppRequest.Response response) {

                        try {
                            System.out.println(new Gson().toJson(response));
                            String newProjectDir = TEMP_DIR;
                            AppRequest.Response.App app = response.apps.get(0);
                            String newProjectPackage = "com.fans." + app.app_package;
                            String googleServicesSettingsDir = "google_play_services";
                            String newAppID = APP_ID;
                            String newAppName = app.app_name;


                            File buildDir = new File(newProjectDir);
                            if (!buildDir.exists()) {
                                System.out.println("error " + buildDir.getAbsolutePath() + " not exist");
                                System.exit(0);
                            }

                            try {
                                String logo_url = response.links.get(app.app_logo_link_id).link_url;
                                URL website = new URL(logo_url);
                                ReadableByteChannel rbc = Channels.newChannel(website.openStream());
                                File logo_image = new File(buildDir, "app\\src\\main\\res\\drawable\\logo_icon.png");
                                if (logo_image.exists())
                                    logo_image.delete();
                                FileOutputStream fos = new FileOutputStream(logo_image);
                                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                            } catch (Exception e){
                                System.out.println("error logo download");
                            }


                            File newGoogleServicesFile = new File(googleServicesSettingsDir + "\\" + newProjectPackage);
                            if (!newGoogleServicesFile.exists()) {
                                System.out.println("error " + newGoogleServicesFile.getAbsolutePath() + " not exist   go to link and generate file "
                                        + "https://console.firebase.google.com/project/quests-141113/overview");
                                System.exit(0);
                            }

                            File manifestFile = new File(buildDir, "\\app\\src\\main\\AndroidManifest.xml");
                            String manifest = new String(Files.readAllBytes(Paths.get(manifestFile.getAbsolutePath())));

                            int beg = manifest.indexOf("package=\"") + "package=\"".length();
                            int end = manifest.indexOf("\"", beg);
                            String oldProjectPackage = manifest.substring(beg, end);

                            String oldProjectPackageRegex = oldProjectPackage.replace(".", "\\.");
                            replaceInFiles(buildDir, oldProjectPackageRegex, newProjectPackage, ".java");
                            replaceInFiles(new File(buildDir, "app\\src\\main\\res"), oldProjectPackageRegex, newProjectPackage, ".xml");

                            String oldProjectPackageDir = oldProjectPackage.replace(".", "\\");
                            String newProjectPackageDir = newProjectPackage.replace(".", "\\");
                            File oldBuildSrcDir = new File(buildDir, "\\app\\src\\main\\java\\" + oldProjectPackageDir);
                            File newBuildSrcDir = new File(buildDir, "\\app\\src\\main\\java\\" + newProjectPackageDir);
                            if (!oldBuildSrcDir.renameTo(newBuildSrcDir)) {
                                System.out.println("error rename " + oldBuildSrcDir.getAbsolutePath() + " to " + newBuildSrcDir.getAbsolutePath());
                                System.exit(1);
                            }

                            File valuesFile = new File(buildDir, "app\\src\\main\\res\\values\\strings.xml");
                            replaceInFile(valuesFile, "app_id\\\">.*<", "app_id\">" + newAppID + "<");
                            replaceInFile(valuesFile, "app_name\\\">.*<", "app_name\">" + newAppName + "<");
                            replaceInFile(valuesFile, "com_vk_sdk_AppId\\\">.*<", "com_vk_sdk_AppId\">" + app.vk_app_id + "<");
                            replaceInFile(valuesFile, "google_maps_api_key\\\">.*<", "google_maps_api_key\">" + app.google_maps_api_key + "<");
                            replaceInFile(valuesFile, "facebook_app_id\\\">.*<", "facebook_app_id\">" + app.fb_app_id + "<");


                            replaceInFile(manifestFile, oldProjectPackageRegex, newProjectPackage);


                            File oldGoogleServicesFile = new File(buildDir, "\\app\\google-services.json");

                            if (!newGoogleServicesFile.exists())
                                System.out.println(newGoogleServicesFile.getAbsolutePath() + " not exist");
                            if (oldGoogleServicesFile.exists())
                                oldGoogleServicesFile.delete();
                            Files.copy(newGoogleServicesFile.toPath(), oldGoogleServicesFile.toPath());

                            File appGradleSettings = new File(buildDir, "\\app\\build.gradle");
                            replaceInFile(appGradleSettings, "applicationId \".*\"", "applicationId \""+newProjectPackage+"\"");
                            replaceInFile(appGradleSettings, "storeFile file\\(\".*\"\\)", "storeFile file(\"c:\\\\keys\\\\fans.jks\")");
                            replaceInFile(appGradleSettings, "storePassword \".*\"", "storePassword \"" + 123123 + "\"");
                            replaceInFile(appGradleSettings, "keyAlias \".*\"", "keyAlias \"" + newProjectPackage + "\"");
                            replaceInFile(appGradleSettings, "keyPassword \".*\"", "keyPassword \"" + 123123 + "\"");

                            File releaseApk = new File(buildDir, "app\\build\\outputs\\apk\\app-release.apk");
                            if (releaseApk.exists())
                                releaseApk.delete();

                            System.out.println("good end");

                        } catch (Exception e) {
                            System.out.println("error end " + e.getMessage());
                            System.exit(1);
                        }

                    }
                },
                new ApiRequest.Error() {
                    @Override
                    public void run(int error_code) {
                        System.out.println("request to host error");
                        System.exit(error_code);
                    }
                }
        );

    }
}
