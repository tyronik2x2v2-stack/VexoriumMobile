package com.vexorium.mobile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public final class MainActivity extends Activity {
    private static final String SERVER_ADDRESS = "ams-game-02.centnodes.net:40065";
    private static final String CLIENT_GUIDE_URL = "https://play.google.com/store/apps/details?id=ro.alynsampmobile.launcher";
    private static final String PREFERRED_CLIENT_PACKAGE = "ro.alynsampmobile.launcher";
    private static final String GTA_PACKAGE = "com.rockstargames.gtasa";
    private static final String GTA_PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=com.rockstargames.gtasa";
    private static final String PREFS = "vexorium_mobile_settings";
    private static final int BG = Color.rgb(8, 9, 14);
    private static final int PANEL = Color.rgb(20, 22, 32);
    private static final int PANEL_LIGHT = Color.rgb(27, 30, 42);
    private static final int RED = Color.rgb(239, 45, 62);
    private static final int WHITE = Color.rgb(244, 246, 250);
    private static final int MUTED = Color.rgb(157, 164, 181);
    private EditText nicknameInput;
    private TextView gtaInstallStatus;
    private TextView gtaInstallButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(BG);
        getWindow().setNavigationBarColor(BG);
        getWindow().getDecorView().setSystemUiVisibility(0);
        setContentView(buildScreen());
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshGtaInstallState();
    }

    private View buildScreen() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(BG);

        LinearLayout page = new LinearLayout(this);
        page.setOrientation(LinearLayout.VERTICAL);
        page.setPadding(dp(20), dp(22), dp(20), dp(26));
        scroll.addView(page, new ScrollView.LayoutParams(-1, -2));

        LinearLayout brandRow = new LinearLayout(this);
        brandRow.setGravity(Gravity.CENTER_VERTICAL);
        ImageView icon = new ImageView(this);
        icon.setImageResource(R.mipmap.ic_launcher);
        icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        brandRow.addView(icon, new LinearLayout.LayoutParams(dp(64), dp(64)));
        LinearLayout brandText = new LinearLayout(this);
        brandText.setOrientation(LinearLayout.VERTICAL);
        brandText.setPadding(dp(13), 0, 0, 0);
        TextView brand = text("VEXORIUM", 26, WHITE, true);
        brand.setLetterSpacing(0.06f);
        TextView mobile = text("M O B I L E   •   ROLEPLAY", 10, RED, true);
        mobile.setLetterSpacing(0.08f);
        brandText.addView(brand);
        brandText.addView(mobile);
        brandRow.addView(brandText, new LinearLayout.LayoutParams(0, -2, 1));
        page.addView(brandRow);

        TextView eyebrow = text("ТВОЙ ГОРОД. ТВОЯ ИСТОРИЯ.", 11, MUTED, true);
        eyebrow.setPadding(0, dp(24), 0, dp(8));
        page.addView(eyebrow);
        TextView title = text("ИГРА НАЧИНАЕТСЯ\nС ТЕБЯ", 29, WHITE, true);
        title.setLineSpacing(dp(1), 1.0f);
        page.addView(title);
        TextView subtitle = text("Мобильный центр подключения VEXORIUM ROLEPLAY", 13, MUTED, false);
        subtitle.setPadding(0, dp(8), 0, dp(22));
        page.addView(subtitle);

        LinearLayout serverCard = card();
        TextView sectionLabel = text("ИГРОВОЙ СЕРВЕР", 10, MUTED, true);
        sectionLabel.setLetterSpacing(0.1f);
        serverCard.addView(sectionLabel);
        TextView serverName = text("VEXORIUM ROLEPLAY", 19, WHITE, true);
        serverName.setPadding(0, dp(9), 0, dp(2));
        serverCard.addView(serverName);
        TextView gameLabel = text("Grand Theft Auto: San Andreas • SA-MP", 11, MUTED, false);
        serverCard.addView(gameLabel);

        LinearLayout ipBox = new LinearLayout(this);
        ipBox.setGravity(Gravity.CENTER_VERTICAL);
        ipBox.setPadding(dp(12), dp(11), dp(12), dp(11));
        ipBox.setBackground(round(PANEL_LIGHT, dp(12), 0, 0));
        LinearLayout ipTexts = new LinearLayout(this);
        ipTexts.setOrientation(LinearLayout.VERTICAL);
        TextView ipLabel = text("АДРЕС СЕРВЕРА", 9, MUTED, true);
        TextView ipValue = text(SERVER_ADDRESS, 14, WHITE, true);
        ipValue.setPadding(0, dp(4), 0, 0);
        ipTexts.addView(ipLabel);
        ipTexts.addView(ipValue);
        ipBox.addView(ipTexts, new LinearLayout.LayoutParams(0, -2, 1));
        TextView copyButton = pillButton("КОПИРОВАТЬ", RED, WHITE);
        ipBox.addView(copyButton, new LinearLayout.LayoutParams(-2, dp(42)));
        copyButton.setOnClickListener(v -> copyServerAddress());
        LinearLayout.LayoutParams ipParams = new LinearLayout.LayoutParams(-1, -2);
        ipParams.topMargin = dp(17);
        serverCard.addView(ipBox, ipParams);

        TextView status = text("●  АДРЕС НАСТРОЕН  •  ОНЛАЙН НЕ ПРОВЕРЯЕТСЯ", 10, MUTED, true);
        status.setPadding(0, dp(14), 0, 0);
        serverCard.addView(status);
        page.addView(serverCard);

        // Official GTA San Andreas installation is handled by Google Play.
        // The Play Store does not expose its live download percentage/ETA to third-party apps.
        LinearLayout gtaCard = card();
        TextView gtaHeading = text("GTA SAN ANDREAS", 16, WHITE, true);
        gtaCard.addView(gtaHeading);
        gtaInstallStatus = text("Проверяем, установлена ли игра...", 12, MUTED, false);
        gtaInstallStatus.setPadding(0, dp(8), 0, dp(12));
        gtaCard.addView(gtaInstallStatus);
        gtaInstallButton = pillButton("УСТАНОВИТЬ GTA SAN ANDREAS", RED, WHITE);
        gtaCard.addView(gtaInstallButton, new LinearLayout.LayoutParams(-1, dp(50)));
        gtaInstallButton.setOnClickListener(v -> openGtaInGooglePlay());
        TextView gtaHint = text("Загрузка и установка выполняются в Google Play. Там отображаются процент и примерное оставшееся время. После установки вернись в VEXORIUM MOBILE — приложение проверит результат.", 10, MUTED, false);
        gtaHint.setPadding(0, dp(10), 0, 0);
        gtaCard.addView(gtaHint);
        page.addView(gtaCard, params(0, -2, 14, 0));
        refreshGtaInstallState();

        LinearLayout profileCard = card();
        TextView nickLabel = text("ТВОЙ ИГРОВОЙ НИК", 10, MUTED, true);
        nickLabel.setLetterSpacing(0.06f);
        profileCard.addView(nickLabel);
        nicknameInput = new EditText(this);
        nicknameInput.setSingleLine(true);
        nicknameInput.setTextSize(15);
        nicknameInput.setTextColor(WHITE);
        nicknameInput.setHintTextColor(Color.rgb(103, 109, 125));
        nicknameInput.setHint("Например, Player_Name");
        nicknameInput.setPadding(dp(13), 0, dp(13), 0);
        nicknameInput.setBackground(round(PANEL_LIGHT, dp(12), dp(1), Color.rgb(48, 51, 65)));
        nicknameInput.setText(getSharedPreferences(PREFS, MODE_PRIVATE).getString("nickname", "Player_Name"));
        LinearLayout.LayoutParams nickParams = new LinearLayout.LayoutParams(-1, dp(52));
        nickParams.topMargin = dp(11);
        profileCard.addView(nicknameInput, nickParams);
        nicknameInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                getSharedPreferences(PREFS, MODE_PRIVATE).edit().putString("nickname", s.toString()).apply();
            }
            @Override public void afterTextChanged(Editable s) { }
        });
        page.addView(profileCard, params(0, dp(150), dp(15), 0));

        TextView play = pillButton("▶   ОТКРЫТЬ МОБИЛЬНЫЙ КЛИЕНТ", RED, WHITE);
        LinearLayout.LayoutParams playParams = new LinearLayout.LayoutParams(-1, dp(54));
        playParams.topMargin = dp(18);
        page.addView(play, playParams);
        play.setOnClickListener(v -> openMobileClient());

        TextView guide = pillButton("КАК УСТАНОВИТЬ И ПОДКЛЮЧИТЬСЯ", PANEL_LIGHT, WHITE);
        LinearLayout.LayoutParams guideParams = new LinearLayout.LayoutParams(-1, dp(50));
        guideParams.topMargin = dp(10);
        page.addView(guide, guideParams);
        guide.setOnClickListener(v -> showGuide());

        TextView note = text("Важно: это лаунчер-помощник, а не сама GTA San Andreas. Он не содержит игровых файлов. Для игры нужен совместимый мобильный клиент и законно полученные файлы игры.", 11, MUTED, false);
        note.setGravity(Gravity.CENTER);
        note.setPadding(dp(8), dp(20), dp(8), dp(12));
        page.addView(note);

        TextView footer = text("VEXORIUM MOBILE  •  КРОССПЛЕЙ-ТЕСТ 1.2", 9, Color.rgb(97, 102, 117), true);
        footer.setGravity(Gravity.CENTER);
        page.addView(footer);
        return scroll;
    }

    private boolean isOfficialGtaInstalled() {
        try {
            getPackageManager().getApplicationInfo(GTA_PACKAGE, 0);
            return true;
        } catch (PackageManager.NameNotFoundException notInstalled) {
            return false;
        }
    }

    private void refreshGtaInstallState() {
        if (gtaInstallStatus == null || gtaInstallButton == null) return;

        if (isOfficialGtaInstalled()) {
            gtaInstallStatus.setText("✓ ОФИЦИАЛЬНАЯ GTA SAN ANDREAS УСТАНОВЛЕНА");
            gtaInstallStatus.setTextColor(Color.rgb(91, 220, 142));
            gtaInstallButton.setVisibility(View.GONE);
            getSharedPreferences(PREFS, MODE_PRIVATE).edit().putBoolean("gtaInstallStarted", false).apply();
        } else {
            boolean previouslyOpenedStore = getSharedPreferences(PREFS, MODE_PRIVATE)
                    .getBoolean("gtaInstallStarted", false);
            gtaInstallStatus.setText(previouslyOpenedStore
                    ? "Игра пока не обнаружена. Проверь ход загрузки в Google Play. После завершения установки вернись сюда."
                    : "GTA San Andreas не найдена на этом телефоне.");
            gtaInstallStatus.setTextColor(MUTED);
            gtaInstallButton.setVisibility(View.VISIBLE);
            gtaInstallButton.setText(previouslyOpenedStore
                    ? "СНОВА ОТКРЫТЬ GOOGLE PLAY"
                    : "УСТАНОВИТЬ GTA SAN ANDREAS");
        }
    }

    private void openGtaInGooglePlay() {
        getSharedPreferences(PREFS, MODE_PRIVATE).edit().putBoolean("gtaInstallStarted", true).apply();
        Uri marketUri = Uri.parse("market://details?id=" + GTA_PACKAGE);
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, marketUri));
        } catch (Exception noPlayStore) {
            openUrl(GTA_PLAY_STORE_URL);
        }
        Toast.makeText(this, "Google Play откроет официальную страницу GTA San Andreas. Вернись сюда после установки.", Toast.LENGTH_LONG).show();
        refreshGtaInstallState();
    }

    private void copyServerAddress() {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newPlainText("VEXORIUM server", SERVER_ADDRESS));
        Toast.makeText(this, "IP сервера скопирован", Toast.LENGTH_SHORT).show();
    }

    private void openMobileClient() {
        hideKeyboard();
        PackageManager pm = getPackageManager();

        // Prefer Alyn SA-MP Mobile, a third-party Android client that supports SA-MP 0.3.7.
        // We do not bundle the GTA game or the third-party client's code.
        Intent preferred = pm.getLaunchIntentForPackage(PREFERRED_CLIENT_PACKAGE);
        if (preferred != null) {
            copyServerAddress();
            try {
                preferred.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(preferred);
                Toast.makeText(this,
                        "Адрес сервера скопирован. В Alyn добавь его через Current Server → + → IP/Port → Check & Add.",
                        Toast.LENGTH_LONG).show();
            } catch (Exception ex) {
                showClientLaunchError(ex);
            }
            return;
        }

        List<ApplicationInfo> apps = new ArrayList<>();
        try {
            for (ApplicationInfo info : pm.getInstalledApplications(PackageManager.GET_META_DATA)) {
                if (info.packageName.equals(getPackageName())) continue;
                CharSequence rawLabel = pm.getApplicationLabel(info);
                String label = rawLabel == null ? "" : rawLabel.toString().toLowerCase(Locale.ROOT);
                String compact = label.replace(" ", "").replace("-", "").replace(":", "");
                boolean match = (compact.contains("samp") && !compact.contains("sample"))
                        || compact.contains("sanandreasmultiplayer")
                        || compact.contains("alynsamp");
                if (match && pm.getLaunchIntentForPackage(info.packageName) != null) apps.add(info);
            }
        } catch (Exception ignored) { }

        if (apps.size() == 1) {
            launchClient(apps.get(0));
            return;
        }
        if (apps.size() > 1) {
            Collections.sort(apps, Comparator.comparing(a -> String.valueOf(pm.getApplicationLabel(a)).toLowerCase(Locale.ROOT)));
            String[] labels = new String[apps.size()];
            for (int i = 0; i < apps.size(); i++) labels[i] = String.valueOf(pm.getApplicationLabel(apps.get(i)));
            new AlertDialog.Builder(this)
                    .setTitle("Выбери мобильный клиент")
                    .setMessage("Адрес VEXORIUM будет скопирован в буфер обмена. В выбранном клиенте может потребоваться добавить его вручную.")
                    .setItems(labels, (dialog, which) -> {
                        copyServerAddress();
                        launchClient(apps.get(which));
                    })
                    .setNegativeButton("Отмена", null)
                    .show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Нужен игровой клиент")
                .setMessage("VEXORIUM MOBILE — это лаунчер, а не сама GTA. Чтобы бегать по игровому миру и подключаться к серверу, установи совместимый SA-MP-клиент для Android и используй собственные законно полученные файлы игры.\n\nРекомендуемый вариант: Alyn SA-MP Mobile из Google Play. После установки нажми «Открыть мобильный клиент», добавь сервер ams-game-02.centnodes.net:40065 через Current Server → + → IP/Port → Check & Add. Совместимость конкретных функций VEXORIUM нужно проверить в игре.")
                .setNegativeButton("Позже", null)
                .setPositiveButton("Открыть Google Play", (dialog, which) -> openUrl(CLIENT_GUIDE_URL))
                .show();
    }

    private void launchClient(ApplicationInfo info) {
        try {
            copyServerAddress();
            Intent launch = getPackageManager().getLaunchIntentForPackage(info.packageName);
            if (launch == null) throw new IllegalStateException("У приложения нет экрана запуска.");
            launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(launch);
            Toast.makeText(this, "Клиент открыт; адрес скопирован. Добавь " + SERVER_ADDRESS + " в список серверов, если он ещё не добавлен.", Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            new AlertDialog.Builder(this)
                    .setTitle("Не удалось открыть клиент")
                    .setMessage("Открой мобильный клиент вручную и добавь сервер " + SERVER_ADDRESS + "\n\n" + ex.getMessage())
                    .setPositiveButton("Понятно", null)
                    .show();
        }
    }

    private void showGuide() {
        new AlertDialog.Builder(this)
                .setTitle("Как подключиться к VEXORIUM")
                .setMessage("1. Установи Alyn SA-MP Mobile из Google Play или используй другой клиент, которому доверяешь. Это сторонний, неофициальный клиент; совместимость VEXORIUM нужно проверить.\n\n"
                        + "2. Используй собственную законно полученную копию GTA San Andreas и следуй инструкциям клиента по подготовке игровых файлов.\n\n"
                        + "3. Введи ник в VEXORIUM MOBILE. Используй тот ник, под которым хочешь играть.\n\n"
                        + "4. Нажми «Копировать» или «Открыть мобильный клиент» — адрес " + SERVER_ADDRESS + " попадёт в буфер обмена.\n\n"
                        + "5. В Alyn открой Current Server → +, укажи адрес сервера и порт 40065, затем нажми Check & Add. Выбери сервер и запусти игру.\n\n"
                        + "6. На ПК подключись к тому же адресу через SA-MP. Проверим, что игроки видят друг друга и что основные меню/интерьеры работают на Android.")
                .setNegativeButton("Закрыть", null)
                .setPositiveButton("Открыть Google Play", (dialog, which) -> openUrl(CLIENT_GUIDE_URL))
                .show();
    }

    private void showClientLaunchError(Exception ex) {
        new AlertDialog.Builder(this)
                .setTitle("Не удалось открыть клиент")
                .setMessage("Открой SA-MP Mobile вручную. Адрес VEXORIUM: " + SERVER_ADDRESS + "\n\n" + ex.getMessage())
                .setPositiveButton("Понятно", null)
                .show();
    }

    private void openUrl(String url) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        } catch (Exception ex) {
            Toast.makeText(this, "Не удалось открыть ссылку", Toast.LENGTH_SHORT).show();
        }
    }

    private void hideKeyboard() {
        try {
            InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(nicknameInput.getWindowToken(), 0);
            nicknameInput.clearFocus();
        } catch (Exception ignored) { }
    }

    private LinearLayout card() {
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(dp(16), dp(16), dp(16), dp(16));
        layout.setBackground(round(PANEL, dp(18), dp(1), Color.rgb(40, 43, 57)));
        return layout;
    }

    private TextView text(String value, int sp, int color, boolean bold) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(sp);
        view.setTextColor(color);
        view.setTypeface(Typeface.create(bold ? "sans-serif" : "sans-serif", bold ? Typeface.BOLD : Typeface.NORMAL));
        view.setGravity(Gravity.CENTER_VERTICAL);
        return view;
    }

    private TextView pillButton(String label, int background, int foreground) {
        TextView view = text(label, 11, foreground, true);
        view.setGravity(Gravity.CENTER);
        view.setPadding(dp(13), dp(8), dp(13), dp(8));
        view.setBackground(round(background, dp(13), 0, 0));
        view.setClickable(true);
        view.setFocusable(true);
        view.setElevation(dp(1));
        return view;
    }

    private GradientDrawable round(int color, int radius, int strokeWidth, int strokeColor) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(radius);
        if (strokeWidth > 0) drawable.setStroke(strokeWidth, strokeColor);
        return drawable;
    }

    private LinearLayout.LayoutParams params(int width, int height, int top, int bottom) {
        LinearLayout.LayoutParams result = new LinearLayout.LayoutParams(width == 0 ? -1 : width, height);
        result.topMargin = dp(top);
        result.bottomMargin = dp(bottom);
        return result;
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
