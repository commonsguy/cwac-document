package com.commonsware.cwac.document.demo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.*
import com.commonsware.cwac.document.DocumentFileCompat
import kotlinx.android.synthetic.main.activity_main.*

private const val REQUEST_OPEN = 1337

class MainActivity : AppCompatActivity() {
  private lateinit var motor: MainMotor

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    motor = ViewModelProviders.of(this)[MainMotor::class.java]

    val adapter = TreeAdapter(layoutInflater, motor)

    items.adapter = adapter
    items.layoutManager = LinearLayoutManager(this)
    items.addItemDecoration(
      DividerItemDecoration(
        this,
        DividerItemDecoration.VERTICAL
      )
    )

    motor.states.observe(
      this,
      Observer { state -> adapter.submitList(state.contents) })
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.actions, menu)

    return super.onCreateOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    if (item.itemId == R.id.open) {
      startActivityForResult(
        Intent(Intent.ACTION_OPEN_DOCUMENT_TREE),
        REQUEST_OPEN
      )

      return true
    }

    return super.onOptionsItemSelected(item)
  }

  override fun onActivityResult(
    requestCode: Int,
    resultCode: Int,
    data: Intent?
  ) {
    if (requestCode == REQUEST_OPEN) {
      if (resultCode == Activity.RESULT_OK) {
        data?.data?.let { motor.load(DocumentFileCompat.fromTreeUri(this, it)) }
      }

      return;
    }

    super.onActivityResult(requestCode, resultCode, data)
  }

  class TreeAdapter(private val inflater: LayoutInflater, private val motor: MainMotor) :
    ListAdapter<RowState, RowHolder>(RowStateDiffer) {
    override fun onCreateViewHolder(
      parent: ViewGroup,
      viewType: Int
    ) = RowHolder(inflater.inflate(R.layout.row, parent, false), motor)

    override fun onBindViewHolder(holder: RowHolder, position: Int) {
      holder.bind(getItem(position))
    }
  }

  class RowHolder(private val root: View, private val motor: MainMotor) :
    RecyclerView.ViewHolder(root) {
    private val icon = root.findViewById<ImageView>(R.id.icon)
    private val displayName = root.findViewById<TextView>(R.id.displayName)

    fun bind(state: RowState) {
      icon.setImageResource(if (state.isDirectory) R.drawable.ic_folder_black_24dp else R.drawable.ic_file_24dp)
      displayName.text = state.displayName
      root.setOnClickListener {
        if (state.isDirectory) motor.load(state.doc)
        else Toast.makeText(
          root.context,
          state.mimeType ?: "unknown MIME type",
          Toast.LENGTH_LONG
        ).show()
      }
    }
  }

  object RowStateDiffer : DiffUtil.ItemCallback<RowState>() {
    override fun areItemsTheSame(
      oldItem: RowState,
      newItem: RowState
    ) = oldItem === newItem

    override fun areContentsTheSame(
      oldItem: RowState,
      newItem: RowState
    ) = oldItem == newItem
  }
}
