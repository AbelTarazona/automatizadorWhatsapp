package com.company.clicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int RESULT_LOAD_IMG = 1996;
    private Button btnComenzar;
    private ImageView ivToSend;

    private Button btnGenerarNumeros;
    private EditText txtNumero, etMensaje;
    private RadioButton cbMil, cbDiezMil, cbCienMil;
    private ListView rvListNumbers;

    private RadioGroup radioGroup;

    private List<String> listNumbers;


    Uri imagenpath;

    int maxNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnComenzar = findViewById(R.id.btnComenzar);
        ivToSend = findViewById(R.id.ivToSend);

        btnGenerarNumeros = findViewById(R.id.btnGenerarNumeros);
        txtNumero = findViewById(R.id.txtNumero);
        etMensaje = findViewById(R.id.etMensaje);
        cbMil = findViewById(R.id.cbMil);
        cbDiezMil = findViewById(R.id.cbDiezMil);
        cbCienMil = findViewById(R.id.cbCienMil);
        rvListNumbers = findViewById(R.id.rvListNumbers);
        radioGroup = findViewById(R.id.group);


        comprobarCantidad();
        maxNumber = 1000;

        cbMil.setOnClickListener(v -> {
            comprobarCantidad();
            txtNumero.setFilters(new InputFilter[] {new InputFilter.LengthFilter(6)});
            maxNumber = 1000;
        });

        cbCienMil.setOnClickListener(v -> {
            comprobarCantidad();
            txtNumero.setFilters(new InputFilter[] {new InputFilter.LengthFilter(3)});
            maxNumber = 100000;
        });

        cbDiezMil.setOnClickListener(v -> {
            comprobarCantidad();
            txtNumero.setFilters(new InputFilter[] {new InputFilter.LengthFilter(4)});
            maxNumber = 10000;
        });


        ivToSend.setOnClickListener(v -> {
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, RESULT_LOAD_IMG);
        });

        listNumbers = new ArrayList<>();

        //btnComenzar.setOnClickListener(v -> automatizer(etMensaje.getText().toString(), imagenpath, listNumbers));
        btnComenzar.setOnClickListener(v -> sendWhatsapp("+51970080002",imagenpath, "asda"));
        //btnComenzar.setOnClickListener(v -> sendMessageToWhatsAppContact("+51993792257"));

        btnGenerarNumeros.setOnClickListener(v -> generateNumbers());
    }

    private void generateNumbers() {
        String prefix = "+51";
        String numberPattern = txtNumero.getText().toString();
        listNumbers = new ArrayList<>();

        String finalNumber = "";

        for (int i = 0; i < maxNumber; i++) {
            finalNumber = prefix + numberPattern;
            finalNumber += String.format("%03d", i);
            listNumbers.add(finalNumber);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_list_item_1, listNumbers
        );

        rvListNumbers.setAdapter(adapter);

    }

    public void automatizer(String message, Uri image, List<String> numbers) {
        if (message.isEmpty())
        {
            Toast.makeText(this, "Debes ingresar un mensaje", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (image == null)
        {
            Toast.makeText(this, "Debes seleccionar una foto", Toast.LENGTH_SHORT).show();
            return;
        }
        else if (numbers.size() == 0) {
            Toast.makeText(this, "Debes generar una lista de números", Toast.LENGTH_SHORT).show();
            return;
        }

        for (String num : numbers)
        {
            //new Handler(Looper.getMainLooper()).postDelayed(() -> sendWhatsapp(num, image, message), 5000);
            sendWhatsapp(num, image, message);
            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendWhatsapp(String number, Uri image, String message) {
        number = number.replace("+", "").replace(" ", "");
        Intent sendIntent = new Intent("android.intent.action.MAIN");
        sendIntent.putExtra(Intent.EXTRA_STREAM, image);
        sendIntent.putExtra("jid", number + "@s.whatsapp.net");
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setPackage("com.whatsapp");
        sendIntent.setType("image/png");
        this.startActivity(sendIntent);
    }

    private void sendMessageToWhatsAppContact(String number) {
        PackageManager packageManager = this.getPackageManager();
        Intent i = new Intent(Intent.ACTION_VIEW);
        try {
            String url = "https://api.whatsapp.com/send?phone=" + number + "&text=" + URLEncoder.encode("Hola", "UTF-8");
            i.setPackage("com.whatsapp");
            i.putExtra(Intent.EXTRA_STREAM, imagenpath);
            i.setType("image/png");
            i.setData(Uri.parse(url));
            if (i.resolveActivity(packageManager) != null) {
                this.startActivity(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                final Uri _imageUri = data.getData();
                imagenpath = _imageUri;
                final InputStream imageStream = getContentResolver().openInputStream(_imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                ivToSend.setImageBitmap(selectedImage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
            }

        }else {
            Toast.makeText(this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }

    public void comprobarCantidad() {
        switch (radioGroup.getCheckedRadioButtonId()) {
            case R.id.cbMil:
                txtNumero.setHint("### ### 000");
                break;
            case R.id.cbDiezMil:
                txtNumero.setHint("### #00 000");
                break;
            case R.id.cbCienMil:
                txtNumero.setHint("### 000 000");
                break;

        }
    }
}
