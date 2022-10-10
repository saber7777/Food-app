package com.example.fasterfood.fragment.gallery

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
//import com.example.fasterfood.R
import android.util.Log
import android.widget.Button


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [GallerysFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GallerysFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(com.example.fasterfood.R.layout.fragment_gallery, container, false)

        val button: Button = view.findViewById<Button>(com.example.fasterfood.R.id.btn_Edit)
//        button.setOnClickListener { view ->
//            Log.e("AAAAAAAAA","AAAAAAAA");
//        }

        button.setOnClickListener(View.OnClickListener {
            Log.e("Onclick", "Onclick")
        })
        // Inflate the layout for this fragment
        return view
    }

}