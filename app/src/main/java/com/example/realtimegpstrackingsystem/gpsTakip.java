package com.example.realtimegpstrackingsystem;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class gpsTakip extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseReference veriTabani;
    public Marker aracKonumu;
    public float kameraYakinlastirmaSeviyesi;
    public int ilkCalistirmaKontrolDegiskeni;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps_takip);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        int ikonYuksekligi = 100, ikonGenisligi = 100;
        BitmapDrawable konumIkonu = (BitmapDrawable)getResources().getDrawable(R.drawable.konumikonu);
        Bitmap konumIkonuBitmapi = konumIkonu.getBitmap();
        final Bitmap kucultulmusKonumIkonu = Bitmap.createScaledBitmap(konumIkonuBitmapi, ikonGenisligi, ikonYuksekligi, false);
        kameraYakinlastirmaSeviyesi = 16.0f;
        ilkCalistirmaKontrolDegiskeni = 0;
        veriTabani = FirebaseDatabase.getInstance().getReference().child("GPS Verileri");
        ValueEventListener gpsVerileriListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                gpsVerileri gpsVerileri = dataSnapshot.getValue(gpsVerileri.class);
                mMap.clear();
                aracKonumu = mMap.addMarker(new MarkerOptions().position(new LatLng(gpsVerileri.enlem, gpsVerileri.boylam))
                        .icon(BitmapDescriptorFactory.fromBitmap(kucultulmusKonumIkonu))
                        .title("Hız: " + Integer.toString(gpsVerileri.hiz) + " km/saat")
                        .snippet("Zaman: " + gpsVerileri.zaman + " Tarih: " + gpsVerileri.tarih));
                aracKonumu.showInfoWindow();
                if(ilkCalistirmaKontrolDegiskeni == 0) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(gpsVerileri.enlem, gpsVerileri.boylam), kameraYakinlastirmaSeviyesi));
                    ilkCalistirmaKontrolDegiskeni = ilkCalistirmaKontrolDegiskeni + 1;
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        veriTabani.addValueEventListener(gpsVerileriListener);
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }
}
