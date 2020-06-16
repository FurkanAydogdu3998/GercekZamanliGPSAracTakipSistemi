package com.example.realtimegpstrackingsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    public TextView tvuygulamaTanitimi, tvhesapOlustur, tvsifreDegistir;
    public EditText etkullaniciAdi, etsifre;
    public Button bgirisYap;
    public ProgressDialog uyariEkrani;
    public FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvuygulamaTanitimi = (TextView)findViewById(R.id.tvuygulamaTanitimi);
        tvhesapOlustur = (TextView)findViewById(R.id.tvhesapOlustur);
        tvsifreDegistir = (TextView)findViewById(R.id.tvsifreDegistir);
        etkullaniciAdi = (EditText)findViewById(R.id.etkullaniciAdi);
        etsifre = (EditText)findViewById(R.id.etsifre);
        bgirisYap = (Button)findViewById(R.id.bgirisYap);
        findViewById(R.id.bgirisYap).setOnClickListener(this);
        findViewById(R.id.tvhesapOlustur).setOnClickListener(this);
        findViewById(R.id.tvsifreDegistir).setOnClickListener(this);
        uyariEkrani = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
    }
    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.bgirisYap:
                girisYap();
                break;
            case R.id.tvhesapOlustur:
                startActivity(new Intent(this, hesapOlustur.class));
                break;
            case R.id.tvsifreDegistir:
                startActivity(new Intent(this, sifreDegistir.class));
            default:
                break;
        }
    }
    public void girisYap() {
        uyariEkrani.setMessage("Giriş Yapılıyor");
        uyariEkrani.show();
        String kullaniciAdi = etkullaniciAdi.getText().toString();
        String sifre = etsifre.getText().toString();
        boolean veriDogrulugu = true;
        if(TextUtils.isEmpty(kullaniciAdi)) {
            Toast.makeText(getApplicationContext(), "Kullanıcı adı alanı boş olamaz.", Toast.LENGTH_LONG).show();
            veriDogrulugu = false;
        }
        if(TextUtils.isEmpty(sifre)) {
            Toast.makeText(getApplicationContext(), "Şifre alanı boş olamaz.", Toast.LENGTH_LONG).show();
            veriDogrulugu = false;
        }
        if(sifre.length() < 6) {
            Toast.makeText(getApplicationContext(), "Şifre 6 haneden az olamaz.", Toast.LENGTH_LONG).show();
            veriDogrulugu = false;
        }
        if(veriDogrulugu == true) {
            String emailHesabi = kullaniciAdi + "@gpssistem.com";
            mAuth.signInWithEmailAndPassword(emailHesabi, sifre)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                uyariEkrani.hide();
                                Toast.makeText(getApplicationContext(), "Giriş başarılı.", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(getApplicationContext(), gpsTakip.class));
                            }
                            else {
                                uyariEkrani.hide();
                                Toast.makeText(getApplicationContext(), "Giriş başarısız.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
        else {
            uyariEkrani.hide();
        }
    }
}
