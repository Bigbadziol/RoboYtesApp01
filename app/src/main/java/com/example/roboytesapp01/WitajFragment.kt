package com.example.roboytesapp01

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


private const val ARG_PARAM_WITAJ1 = "param_witaj1"
private const val ARG_PARAM_WITAJ2 = "param_witaj2"

/**
 * A simple [Fragment] subclass.
 * Use the [WitajFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WitajFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM_WITAJ1)
            param2 = it.getString(ARG_PARAM_WITAJ2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_witaj, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment WitajFragment.
         */

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            WitajFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM_WITAJ1, param1)
                    putString(ARG_PARAM_WITAJ2, param2)
                }
            }
    }
}