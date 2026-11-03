package com.bg7yoz.ft8cn.ui;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bg7yoz.ft8cn.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.EnumMap;
import java.util.Map;

/**
 * 日志网页地址对话框：显示网址、二维码，并提供“复制网址 / 打开”按钮。
 *
 * @author BH3XZT
 */
public class LogServerDialog extends Dialog {

    private final String url;

    public LogServerDialog(Context context, String url) {
        super(context, R.style.HelpDialog);
        this.url = url;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.log_server_dialog_layout);

        TextView urlTextView = (TextView) findViewById(R.id.logServerUrlTextView);
        urlTextView.setText(url);

        ImageView qrImageView = (ImageView) findViewById(R.id.logServerQrImageView);
        Bitmap qrBitmap = createQrBitmap(url, 480);
        if (qrBitmap != null) {
            qrImageView.setImageBitmap(qrBitmap);
        } else {
            qrImageView.setVisibility(View.GONE);
        }

        Button copyButton = (Button) findViewById(R.id.copyUrlButton);
        copyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClipboardManager clipboard = (ClipboardManager) getContext()
                        .getSystemService(Context.CLIPBOARD_SERVICE);
                if (clipboard != null) {
                    clipboard.setPrimaryClip(ClipData.newPlainText("FT8CN log url", url));
                    Toast.makeText(getContext(), R.string.url_copied, Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button openButton = (Button) findViewById(R.id.openUrlButton);
        openButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    getContext().startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button closeButton = (Button) findViewById(R.id.closeLogServerButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    /**
     * 生成二维码位图（zxing core，纯 Java）。
     */
    private Bitmap createQrBitmap(String content, int size) {
        if (content == null || content.length() == 0) {
            return null;
        }
        try {
            Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 1);
            BitMatrix matrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE
                    , size, size, hints);
            int[] pixels = new int[size * size];
            for (int y = 0; y < size; y++) {
                for (int x = 0; x < size; x++) {
                    pixels[y * size + x] = matrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF;
                }
            }
            Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, size, 0, 0, size, size);
            return bitmap;
        } catch (Exception e) {
            return null;
        }
    }
}
