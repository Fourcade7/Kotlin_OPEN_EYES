package com.fourcade7.kotlin_open_eyes

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationRequest
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.*
import com.google.android.gms.location.LocationRequest.create
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    val REQUEST_IMAGE_CAPTURE = 1
    lateinit var databaseReference: DatabaseReference
    val arraylist = ArrayList<Svetafor>()
    lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var locationRequest: com.google.android.gms.location.LocationRequest
    val REQUEST_CHECK_SETTINGS = 10001;

    companion object {
        var lat: Double? = null
        var lon: Double? = null

    }
    var a=0
    var b=0

    var lat1: Double? = null
    var lon1: Double? = null

    var camera = true

    //update lccation
    lateinit var locationCallback: LocationCallback
    var idgroup = LatLng(41.5512370, 60.6271150)

    //for map
    lateinit var googleMap: GoogleMap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //opencamera()

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Svetafors")
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this@MainActivity)
        readfromdatabase()
        buildLocationRequest()
        startLocationUpdates()
        //lastlocation()
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.googlemap) as SupportMapFragment
        mapFragment.getMapAsync(this)





    }

    fun opencamera(){
        if (ContextCompat.checkSelfPermission(
                applicationContext,Manifest.permission.CAMERA
            )== PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CAMERA),
               1
            )
        /**set camera Open*/

            val cameraInt = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraInt,1)


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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.mymenu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item1 -> {
                startActivity(Intent(this@MainActivity, MainActivity2::class.java))
            }
            R.id.item2 -> {
                startActivity(Intent(this@MainActivity, MainActivity3::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }


    fun lastlocation() {
        if (ActivityCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                101
            )

            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener {
            if (it != null) {

                Toast.makeText(
                    this@MainActivity,
                    "${it.latitude} / ${it.longitude}",
                    Toast.LENGTH_LONG
                ).show()

//                lat=it.latitude
//                lon=it.longitude


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


                    GlobalScope.launch(Dispatchers.Main) {
                        delay(500)
                        lat = location.latitude
                        lon = location.longitude
                        onMapReady(googleMap)
                        a++
                    }
                    //  Toast.makeText(this@MainActivity, " $lat / $lon", Toast.LENGTH_SHORT).show()


                }

            }
        }
        if (ActivityCompat.checkSelfPermission(
                this@MainActivity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this@MainActivity,
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
                   if (a<5){
                       markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.qizil2))
                       googleMap.addMarker(markerOptions)
                       b=1
                   }
                    if (a>5 && a<10){
                        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.sariq2))
                        googleMap.addMarker(markerOptions)
                        b=2
                    }
                    if (a>10 && a<15){

                            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.yashil2))
                        googleMap.addMarker(markerOptions)
                        b=3

                    }
                    if (a>15){
                        a=0
                    }

                    var location1 = Location("Beruniy")//41.67739/60.738
                    location1.latitude = lat as Double
                    location1.longitude = lon as Double
                    var location2 = Location("Svetafor")
                    location2.latitude = lat1 as Double
                    location2.longitude = lon1 as Double
                    var distance = location1.distanceTo(location2)
                    //Toast.makeText(this@MainActivity, "$distance m", Toast.LENGTH_SHORT).show()
                    val mediaPlayer: MediaPlayer
                    val mediaPlayer2: MediaPlayer
                    val mediaPlayer3: MediaPlayer
                    val mediaPlayer4: MediaPlayer

                    mediaPlayer = MediaPlayer.create(this@MainActivity, R.raw.camera)
                    mediaPlayer2 = MediaPlayer.create(this@MainActivity, R.raw.red)
                    mediaPlayer3 = MediaPlayer.create(this@MainActivity, R.raw.yellow)
                    mediaPlayer4 = MediaPlayer.create(this@MainActivity, R.raw.green)


                    if (distance < 6.0) {

                        mediaPlayer.start()
                        delay(1000)
                        if (b==1){
                            mediaPlayer2.start()
                            opencamera()
                        }else{
                            mediaPlayer2.stop()

                        }
                        if (b==2){
                            mediaPlayer3.start()
                            opencamera()

                        }else{
                            mediaPlayer3.stop()
                        }
                        if (b==3){
                            mediaPlayer4.start()
                            opencamera()

                        }else{
                            mediaPlayer4.stop()
                        }




                    } else {
                        mediaPlayer.stop()

                    }

                        //.icon(BitmapDescriptorFactory.fromResource(R.drawable.qizil2))

                    //Toast.makeText(this@MainActivity2, i.toString(), Toast.LENGTH_SHORT).show()
                }

            }
        }

//        if (lat != null && lon != null) {
//            googleMap.clear()
//            var lastlocation = LatLng(lat!!, lon!!)
//            var markerOptions = MarkerOptions()
//                .position(lastlocation)
//                .title("Pr")
//            googleMap.addMarker(markerOptions)
//            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastlocation, 15f))
//            var location1 = Location("Beruniy")//41.67739/60.738
//            location1.latitude = lat as Double
//            location1.longitude = lon as Double
//            var location2 = Location("Svetafor")
//            location2.latitude = 41.6766009
//            location2.longitude = 60.7388095
//            var distance = location1.distanceTo(location2)
//            //Toast.makeText(this@MainActivity, "$distance m", Toast.LENGTH_SHORT).show()
//            val mediaPlayer: MediaPlayer
//            mediaPlayer = MediaPlayer.create(this@MainActivity, R.raw.windowserror)
//            if (distance < 7.0) {
//
//                mediaPlayer.start()
//            } else {
//                mediaPlayer.stop()
//            }
//        }

    }


}