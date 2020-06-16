package com.example.realtimegpstrackingsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class hesapOlustur extends AppCompatActivity implements View.OnClickListener{
    public TextView tvekranTanitim, tvgirisEkraninaDonus;
    public EditText etkullaniciAdi, etsifre, etsifreTekrari;
    public Button bhesapOlustur;
    public ProgressDialog uyariEkrani;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hesap_olustur);
        tvekranTanitim = (TextView)findViewById(R.id.tvekranTanitim);
        tvgirisEkraninaDonus = (TextView)findViewById(R.id.tvgirisEkraninaDonus);
        etkullaniciAdi = (EditText)findViewById(R.id.etkullaniciAdi);
        etsifre = (EditText)findViewById(R.id.etsifre);
        etsifreTekrari = (EditText)findViewById(R.id.etsifreTekrari);
        findViewById(R.id.bhesapOlustur).setOnClickListener(this);
        findViewById(R.id.tvgirisEkraninaDonus).setOnClickListener(this);
        uyariEkrani = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
    }
    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.bhesapOlustur:
                hesapOlustur();
                break;
            case R.id.tvgirisEkraninaDonus:
                startActivity(new Intent(this, MainActivity.class));
                break;
            default:
                break;
        }
    }
    public void hesapOlustur() {
        uyariEkrani.setMessage("Hesap Oluşturuluyor");
        uyariEkrani.show();
        String kullaniciAdi = etkullaniciAdi.getText().toString();
        String sifre = etsifre.getText().toString();
        String sifreTekrari = etsifreTekrari.getText().toString();
        boolean veriDogrulugu = true;
        if(TextUtils.isEmpty(kullaniciAdi)) {
            Toast.makeText(getApplicationContext(), "Kullanıcı adı alanı boş bırakılamaz.", Toast.LENGTH_LONG).show();
            veriDogrulugu = false;
        }
        if(TextUtils.isEmpty(sifre)) {
            Toast.makeText(getApplicationContext(), "Şifre alanı boş bırakılamaz.", Toast.LENGTH_LONG).show();
            veriDogrulugu = false;
        }
        if(TextUtils.isEmpty(sifreTekrari)) {
            Toast.makeText(getApplicationContext(), "Şifre tekrarı alanı boş bırakılamaz.", Toast.LENGTH_LONG).show();
            veriDogrulugu = false;
        }
        if(sifre.length() < 6) {
            Toast.makeText(getApplicationContext(), "Şifre 6 haneden az olamaz.", Toast.LENGTH_LONG).show();
            veriDogrulugu = false;
        }
        if(sifre.compareTo(sifreTekrari) != 0) {
            Toast.makeText(getApplicationContext(), "Şifre ve şifre tekrarı birbirinden farklı olamaz.", Toast.LENGTH_LONG).show();
            veriDogrulugu = false;
        }
        if(veriDogrulugu == true) {
            String emailHesabi = kullaniciAdi + "@gpssistem.com";
            mAuth.createUserWithEmailAndPassword(emailHesabi, sifre)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                uyariEkrani.hide();
                                Toast.makeText(getApplicationContext(), "Hesap oluşturma işlemi başarılı.", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(getApplicationContext(), gpsTakip.class));
                            }
                            else {
                                uyariEkrani.hide();
                                Toast.makeText(getApplicationContext(), "Hesap oluşturma işlemi başarısız.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
        else {
            uyariEkrani.hide();
        }
    }
}
