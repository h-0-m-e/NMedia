package ru.netology.nmedia.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.ActivityNewPostBinding

class NewPostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        val binding = ActivityNewPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.content.setText(intent?.getStringExtra(Intent.EXTRA_TEXT))

        binding.buttonEnter.setOnClickListener{
            val text = binding.content.text.toString()
            if (text.isNullOrBlank()) {
                Toast.makeText(
                    this@NewPostActivity,
                    R.string.empty_content_warning,
                    Toast.LENGTH_SHORT
                ).show()
                setResult(Activity.RESULT_CANCELED)
            }else {
                setResult(Activity.RESULT_OK, Intent().apply { putExtra(Intent.EXTRA_TEXT, text) })
            }
            finish()
        }
    }
    object NewPostContract: ActivityResultContract<Unit, String?>() {
        override fun createIntent(context: Context, input: Unit) =
            Intent(context, NewPostActivity::class.java)

        override fun parseResult(resultCode: Int, intent: Intent?) =
            intent?.getStringExtra(Intent.EXTRA_TEXT)
    }
}
