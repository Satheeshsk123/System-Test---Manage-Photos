package com.test.systemtest.view



import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.test.systemtest.MainAdapter
import com.test.systemtest.R
import com.test.systemtest.databinding.ActivityMainBinding
import com.test.systemtest.viewmodel.MainViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date


class MainActivity : AppCompatActivity(),ImageClickListener {

    companion object {
        private const val CAMERA_PERMISSION_CODE = 100
    }
    var vFilename: String = ""

    lateinit var binding: ActivityMainBinding
    lateinit var viewModel: MainViewModel
    var mainAdapter: MainAdapter?=null
    var imageSize:Int=0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this, R.layout.activity_main)

        viewModel=ViewModelProvider(this)[MainViewModel::class.java]
        binding.mainViewModel=viewModel

        binding.addPhotosButton.setOnClickListener {
            if (imageSize>=6){
                Toast.makeText(applicationContext,"You have reached maximum number of photos",Toast.LENGTH_SHORT).show()
            }else{
                bottomSheet()
            }
        }

        binding.addImageCard.setOnClickListener {
            bottomSheet()
        }

        prepareRecyclerView()
        initObserver()
    }

    fun prepareRecyclerView(){
        val layoutManager = GridLayoutManager(this, 2)
        binding.imageRecyler.layoutManager=layoutManager
        mainAdapter= MainAdapter()
        binding.imageRecyler.adapter=mainAdapter
    }

    fun initObserver(){
        viewModel.imageList.observe(this, Observer {
            it.let {
                mainAdapter!!.setMovieList(it,this)
                imageSize=it.size
                checkCondition(it.size)
            }
        })
    }

    fun checkCondition(imageSize:Int) {
        if (imageSize==0){
            binding.addImageCard.visibility=View.VISIBLE
            binding.titleText.setText(getString(R.string.photos_are_the_first_thing))
        }else{
            binding.addImageCard.visibility=View.GONE
            binding.titleText.setText(getString(R.string.drag_photos_to_reorder))
        }
    }


    private fun openCamera() {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        vFilename = "ListPhoto_" + timeStamp + ".jpg"
        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val file = File(path, vFilename)
        val image_uri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", file);

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        cameraImageCap.launch(cameraIntent)
    }

    fun galleryPicker(){
        val pickImg = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        galleryImagePicker.launch(pickImg)
    }

    private val galleryImagePicker = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data = it.data
                val imgUri = data?.data
                viewModel.addImageData(imgUri.toString())
            }
        }

    private val cameraImageCap = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                val file = File(path, vFilename)
                val uri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", file);
                viewModel.addImageData(uri.toString())
            }
        }


    fun bottomSheet(){
        val dialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog, null)
        val galleryLay = view.findViewById<ConstraintLayout>(R.id.galleryLay)
        val cameraLay = view.findViewById<ConstraintLayout>(R.id.cameraLay)
        val whatsappLay = view.findViewById<ConstraintLayout>(R.id.whatsappLay)

        galleryLay.setOnClickListener {
            galleryPicker()
            dialog.dismiss()
        }
        cameraLay.setOnClickListener {
            checkPermission(Manifest.permission.CAMERA, CAMERA_PERMISSION_CODE)
            dialog.dismiss()
        }
        whatsappLay.setOnClickListener {
            redirectWhatsapp()
            dialog.dismiss()
        }
        dialog.setCancelable(true)
        dialog.setContentView(view)
        dialog.show()
    }

    fun redirectWhatsapp(){
        val number="9111111111"
        val url = "https://api.whatsapp.com/send?phone=$number"
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        startActivity(i)
    }

    override fun onImageCancelClick(clickPosition: Int) {
        deleteImageDailog(clickPosition)
    }

    fun deleteImageDailog(clickPosition: Int){
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.delete_dialog)

        val yesBtn = dialog.findViewById(R.id.yesButton) as Button
        val noBtn = dialog.findViewById(R.id.noButton) as Button

        yesBtn.setOnClickListener {
            viewModel.removeImageData(clickPosition)
            dialog.dismiss()
        }

        noBtn.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }


    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(this@MainActivity, permission) == PackageManager.PERMISSION_DENIED) {
            // Requesting the permission
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(permission), requestCode)
        } else {
            openCamera()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this@MainActivity, "Camera Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@MainActivity, "Camera Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

}