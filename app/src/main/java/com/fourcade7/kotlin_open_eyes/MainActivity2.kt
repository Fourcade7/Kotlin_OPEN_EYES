package com.fourcade7.kotlin_open_eyes

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main2.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity2 : AppCompatActivity(), OnMapReadyCallback {
    lateinit var databaseReference: DatabaseReference
    lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var locationRequest: com.google.android.gms.location.LocationRequest
    lateinit var locationCallback: LocationCallback
    var lat: Double? = null
    var lon: Double? = null

    var lat1: Double? = null
    var lon1: Double? = null
    lateinit var latLng: LatLng
    lateinit var googleMap: GoogleMap


    val arraylist = ArrayList<Svetafor>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        title = "Traffic light"
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.googlemap2) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this@MainActivity2)

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Svetafors")
        readfromdatabase()
        buildLocationRequest()
        startLocationUpdates()


        button1.setOnClickListener {
            if (lat != null && lon != null) {
                val svetafor = Svetafor(lat!!, lon!!)
                databaseReference.push().setValue(svetafor)
                Toast.makeText(this@MainActivity2, "Added Succesfully", Toast.LENGTH_LONG).show()

            }
        }


    }

    fun lastlocation() {
        if (ActivityCompat.checkSelfPermission(
                this@MainActivity2,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this@MainActivity2,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this@MainActivity2,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                101
            )

            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener {
            if (it != null) {

                Toast.makeText(
                    this@MainActivity2,
                    "${it.latitude} / ${it.longitude}",
                    Toast.LENGTH_LONG
                ).show()

                lat = it.latitude
                lon = it.longitude


            }
        }
    }


    fun buildLocationRequest() {
        locationRequest = com.google.android.gms.location.LocationRequest.create()
        locationRequest.priority =
            com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5000
        locationRequest.fastestInterval = 5000
    }

    fun startLocationUpdates() {

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                p0 ?: return

                for (location in p0.locations) {
                    // Log.d("Pr", "${location.latitude} / ${location.longitude}")
                    lat = location.latitude
                    lon = location.longitude
                    onMapReady(googleMap)
                    //  Toast.makeText(this@MainActivity, " $lat / $lon", Toast.LENGTH_SHORT).show()


                }

            }
        }
        if (ActivityCompat.checkSelfPermission(
                this@MainActivity2,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this@MainActivity2,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    override fun onMapReady(p0: GoogleMap) {
        googleMap = p0
        if (lat != null && lon != null) {
            googleMap.clear()
            var lastlocation = LatLng(lat!!, lon!!)
            var markerOptions = MarkerOptions()
                .position(lastlocation)
                .title("Traffic Light")
            //.icon(BitmapDescriptorFactory.fromResource(R.drawable.qizil2))
            googleMap.addMarker(markerOptions)
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastlocation, 15f))


            GlobalScope.launch(Dispatchers.Main) {

                delay(1000)


                for (i in 0..arraylist.size - 1) {
                    delay(600)
                    var svetafors = readlatlang(i)
                    var markerOptions = MarkerOptions()
                        .position(svetafors)
                        .title("Traffic Light")
                        .snippet("$i")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.qizil2))
                    googleMap.addMarker(markerOptions)
                    //Toast.makeText(this@MainActivity2, i.toString(), Toast.LENGTH_SHORT).show()
                }

            }

//            var lastlocation2 = LatLng(41.6756407, 60.7386045)
//            var markerOptions2= MarkerOptions()
//                .position(lastlocation2)
//                .title("Traffic Light2")
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.yashil2))
//            googleMap.addMarker(markerOptions2)

        }
    }

    fun readfromdatabase() {
        databaseReference.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(snapshot: DataSnapshot) {
                arraylist.clear()
                for (datasnapshot in snapshot.children) {
                    val svetafor = datasnapshot.getValue(Svetafor::class.java)
                    arraylist.add(svetafor!!)
                }
                //Toast.makeText(this@MainActivity2, "${arraylist.get(1).lat.toString()} \n${arraylist.get(0).lat.toString()}", Toast.LENGTH_SHORT).show()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    fun readlatlang(i: Int): LatLng {

        lat1 = arraylist.get(i).lat
        lon1 = arraylist.get(i).lon

        return LatLng(lat1!!, lon1!!)
    }
}