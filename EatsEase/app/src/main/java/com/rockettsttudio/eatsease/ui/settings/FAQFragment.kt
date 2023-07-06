package com.rockettsttudio.eatsease.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rockettsttudio.eatsease.R

class FAQFragment : DialogFragment() {
    private lateinit var faqRecyclerView: RecyclerView
    private lateinit var faqAdapter: FAQAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_faq, container, false)
        faqRecyclerView = view.findViewById(R.id.faqRecyclerView)
        faqRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val faqList =
            getFAQList()
        faqAdapter = FAQAdapter(faqList)
        faqRecyclerView.adapter = faqAdapter
    }

    private fun getFAQList(): List<FAQItem> {
        return listOf(
            FAQItem("¿Cómo puedo cerrar sesion?", "Presiona Logout el boton rojo"),
            FAQItem("¿Como guardar recetas que quiero realizar despues?", "Entras a la receta que te gusta y en la esquina superio derecha de la imagen de la receta hay un corazon lo presionas y aparecera en saved"),
            FAQItem("¿Puedo compartir recetas que me gustan?", "Si, entras a la receta y desliza hasta el final y saldra el boton de compartir para que puedas compartirlo con tus amigos"),
            FAQItem("¿Como guardar la receta segun el dia que la quiero hacer", "Entras a la receta que quieres planear deslizas hasta abajo y esta el simbolo de calendario le das click y te redirigira a tu calendario para agendarla"),
            FAQItem("¿Como contactarnos?", "Puedes escribirnos a nuestro correo o entrar a nuestra pagina web a traves del help center "),

        )
    }

    data class FAQItem(val question: String, val answer: String)

    class FAQAdapter(private val faqList: List<FAQItem>) :
        RecyclerView.Adapter<FAQAdapter.FAQViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FAQViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_faq, parent, false)
            return FAQViewHolder(view)
        }

        override fun onBindViewHolder(holder: FAQViewHolder, position: Int) {
            val faqItem = faqList[position]
            holder.questionTextView.text = faqItem.question
            holder.answerTextView.text = faqItem.answer
        }

        override fun getItemCount(): Int {
            return faqList.size
        }

        class FAQViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val questionTextView: TextView = itemView.findViewById(R.id.questionTextView)
            val answerTextView: TextView = itemView.findViewById(R.id.answerTextView)
        }
    }
}
