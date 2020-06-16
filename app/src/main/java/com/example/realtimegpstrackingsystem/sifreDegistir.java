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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class sifreDegistir extends AppCompatActivity implements View.OnClickListener{
    public TextView tvekranTanitimi, tvgirisEkraninaDonus;
    public EditText etkullaniciAdi, etsifre, etyeniSifre, etyeniSifreTekrari;
    public Button bsifreDegistir;
    ProgressDialog uyariEkrani;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sifre_degistir);
        tvekranTanitimi = (TextView)findViewById(R.id.tvekranTanitimi);
        tvgirisEkraninaDonus = (TextView)findViewById(R.id.tvgirisEkraninaDonus);
        etkullaniciAdi = (EditText)findViewById(R.id.etkullaniciAdi);
        etsifre = (EditText)findViewById(R.id.etsifre);
        etyeniSifre = (EditText)findViewById(R.id.etyeniSifre);
        etyeniSifreTekrari = (EditText)findViewById(R.id.etyeniSifreTekrari);
        findViewById(R.id.bsifreDegistir).setOnClickListener(this);
        findViewById(R.id.tvgirisEkraninaDonus).setOnClickListener(this);
        uyariEkrani = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
    }
    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.bsifreDegistir:
                sifreDegistir();
                break;
            case R.id.tvgirisEkraninaDonus:
                startActivity(new Intent(this, MainActivity.class));
                break;
            default:
                break;
        }
    }
    public void sifreDegistir() {
        uyariEkrani.setMessage("Şifre Değiştiriliyor");
        uyariEkrani.show();
        String kullaniciAdi = etkullaniciAdi.getText().toString();
        String sifre = etsifre.getText().toString();
        final String yeniSifre = etyeniSifre.getText().toString();
        String yeniSifreTekrari = etyeniSifreTekrari.getText().toString();
        boolean veriDogrulugu = true;
        if(TextUtils.isEmpty(kullaniciAdi)) {
            Toast.makeText(getApplicationContext(), "Kullanıcı adı alanı boş bırakılamaz.", Toast.LENGTH_LONG).show();
            veriDogrulugu = false;
        }
        if(TextUtils.isEmpty(sifre)) {
            Toast.makeText(getApplicationContext(), "Şifre alanı boş bırakılamaz.", Toast.LENGTH_LONG).show();
            veriDogrulugu = false;
        }
        if(TextUtils.isEmpty(yeniSifre)) {
            Toast.makeText(getApplicationContext(), "Yeni şifre alanı boş bırakılamaz.", Toast.LENGTH_LONG).show();
            veriDogrulugu = false;
        }
        if(TextUtils.isEmpty(yeniSifreTekrari)) {
            Toast.makeText(getApplicationContext(), "Yeni şifre tekrarı alanı boş bırakılamaz.", Toast.LENGTH_LONG).show();
            veriDogrulugu = false;
        }
        if(sifre.length() < 6) {
            Toast.makeText(getApplicationContext(), "Şifre 6 haneden az olamaz.", Toast.LENGTH_LONG).show();
            veriDogrulugu = false;
        }
        if(yeniSifre.length() < 6) {
            Toast.makeText(getApplicationContext(), "Yeni şifre 6 haneden az olamaz.", Toast.LENGTH_LONG).show();
            veriDogrulugu = false;
        }
        if(yeniSifre.compareTo(yeniSifreTekrari) != 0) {
            Toast.makeText(getApplicationContext(), "Yeni şifre ile yeni şifre tekrarı farklı olamaz.", Toast.LENGTH_LONG).show();
            veriDogrulugu = false;
        }
        if(veriDogrulugu == true) {
            String emailHesabi = kullaniciAdi + "@gpssistem.com";
            final AuthCredential credential = EmailAuthProvider.getCredential(emailHesabi, sifre);
            mAuth.signInWithEmailAndPassword(emailHesabi, sifre)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                final FirebaseUser hesap = mAuth.getCurrentUser();
                                hesap.reauthenticate(credential)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()) {
                                                    hesap.updatePassword(yeniSifre).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()) {
                                                                uyariEkrani.hide();
                                                                Toast.makeText(getApplicationContext(), "Şifre değiştirme işlemi başarılı.", Toast.LENGTH_LONG).show();
                                                                startActivity(new Intent(getApplicationContext(), gpsTakip.class));
                                                            }
                                                            else {
                                                                uyariEkrani.hide();
                                                                Toast.makeText(getApplicationContext(), "Şifre değiştirme işlemi başarısız.", Toast.LENGTH_LONG).show();
                                                            }
                                                        }
                                                    });
                                                }
                                                else {
                                                    uyariEkrani.hide();
                                                    Toast.makeText(getApplicationContext(), "Şifre değiştirme işlemi sırasında bir sıkıntı oluştu.", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        });
                            }
                            else {
                                uyariEkrani.hide();
                                Toast.makeText(getApplicationContext(), "Hesaba giriş yapılırken bir sıkıntı oluştu.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
        else {
            uyariEkrani.hide();
        }
    }
}
