package com.example.simplearrayadapter.adapter

import android.widget.TextView
import androidx.core.view.descendants
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.annotation.ArrayRes
import androidx.viewbinding.ViewBinding

private class SimpleSingleItemArrayAdapter
<
	ViewVB : ViewBinding,
	DropDownViewVB : ViewBinding,
	out ItemT : Any,
	>(
	context: Context,
	private val inflateDropDownView: ((LayoutInflater, ViewGroup, Boolean) -> DropDownViewVB)? = null,
	private val onBindDropDownView: (SimpleSingleItemArrayAdapter<ViewVB, DropDownViewVB, ItemT>.(
		binding: DropDownViewVB,
		item: ItemT,
		position: Int,
	) -> Unit)? = null,
	private val inflateView: (LayoutInflater, ViewGroup, Boolean) -> ViewVB,
	private val onBindView: SimpleSingleItemArrayAdapter<ViewVB, DropDownViewVB, ItemT>.(
		binding: ViewVB,
		item: ItemT,
		position: Int,
	) -> Unit = { binding, item, _ ->
		defaultArrayAdapterBind(binding, item)
	},
) : ArrayAdapter<@UnsafeVariance ItemT>(context, 0) {

	override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

		val layoutInflater = LayoutInflater.from(parent.context)

		@Suppress("UNCHECKED_CAST")
		val binding = convertView?.tag as? ViewVB ?: inflateView(layoutInflater, parent, false).apply { root.tag = this }

		val item = getItem(position)!!

		onBindView(binding, item, position)

		return binding.root
	}

	override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {

		val layoutInflater = LayoutInflater.from(parent.context)

		val item = getItem(position)!!

		val binding = if (inflateDropDownView == null) {
			if (onBindDropDownView != null) {
				@Suppress("UNCHECKED_CAST")
				val binding = convertView?.tag as? DropDownViewVB ?: inflateView(layoutInflater, parent, false).apply { root.tag = this } as DropDownViewVB

				onBindDropDownView.invoke(this, binding, item, position)

				binding
			}
			else {
				@Suppress("UNCHECKED_CAST")
				val binding = convertView?.tag as? ViewVB ?: inflateView(layoutInflater, parent, false).apply { root.tag = this }

				onBindView(binding, item, position)

				binding
			}
		}
		else {
			if (onBindDropDownView != null) {
				@Suppress("UNCHECKED_CAST")
				val binding = convertView?.tag as? DropDownViewVB ?: inflateDropDownView.invoke(layoutInflater, parent, false).apply { root.tag = this }

				onBindDropDownView.invoke(this, binding, item, position)

				binding
			}
			else {
				@Suppress("UNCHECKED_CAST")
				val binding = convertView?.tag as? ViewVB ?: inflateView.invoke(layoutInflater, parent, false).apply { root.tag = this }

				onBindView(binding, item, position)

				binding
			}
		}

		return binding.root
	}
}

@Suppress("FunctionName")
fun <ViewVB : ViewBinding> TextArrayAdapter(
	context: Context,
	inflateView: (LayoutInflater, ViewGroup, Boolean) -> ViewVB,
): ArrayAdapter<CharSequence> = SimpleSingleItemArrayAdapter<ViewVB, Nothing, CharSequence>(
	context = context,
	inflateView = inflateView,
)

@Suppress("FunctionName")
fun <ViewVB : ViewBinding> TextArrayAdapter(
	context: Context,
	@ArrayRes arrayRes: Int,
	inflateView: (LayoutInflater, ViewGroup, Boolean) -> ViewVB,
): ArrayAdapter<CharSequence> = SimpleSingleItemArrayAdapter<ViewVB, Nothing, CharSequence>(
	context = context,
	inflateView = inflateView,
).apply()
{
	addAll(context.resources.getTextArray(arrayRes).toList())
}

@Suppress("FunctionName")
fun <ViewVB : ViewBinding, ItemT : Any> SimpleArrayAdapter(
	context: Context,
	inflateView: (LayoutInflater, ViewGroup, Boolean) -> ViewVB,
): ArrayAdapter<ItemT> = SimpleSingleItemArrayAdapter<ViewVB, Nothing, ItemT>(
	context = context,
	inflateView = inflateView,
)

@Suppress("FunctionName")
fun <ViewVB : ViewBinding, ItemT : Any> SimpleArrayAdapter(
	context: Context,
	inflateView: (LayoutInflater, ViewGroup, Boolean) -> ViewVB,
	onBindView: ArrayAdapter<ItemT>.(
		binding: ViewVB,
		item: ItemT,
		position: Int,
	) -> Unit,
): ArrayAdapter<ItemT> = SimpleSingleItemArrayAdapter(
	context = context,
	inflateView = inflateView,
	onBindView = onBindView,
	onBindDropDownView = onBindView,
)

@Suppress("FunctionName")
fun <ViewVB : ViewBinding, ItemT : Any> SimpleArrayAdapter(
	context: Context,
	inflateView: (LayoutInflater, ViewGroup, Boolean) -> ViewVB,
	onBindDropDownView: ArrayAdapter<ItemT>.(
		binding: ViewVB,
		item: ItemT,
		position: Int,
	) -> Unit,
	onBindView: ArrayAdapter<ItemT>.(
		binding: ViewVB,
		item: ItemT,
		position: Int,
	) -> Unit,
): ArrayAdapter<ItemT> = SimpleSingleItemArrayAdapter(
	context = context,
	inflateView = inflateView,
	onBindView = onBindView,
	onBindDropDownView = onBindDropDownView,
)


@Suppress("FunctionName")
fun <ViewVB : ViewBinding, ItemT : Any, DropDownViewVB : ViewBinding> SimpleArrayAdapter(
	context: Context,
	inflateView: (LayoutInflater, ViewGroup, Boolean) -> ViewVB,
	inflateDropDownView: (LayoutInflater, ViewGroup, Boolean) -> DropDownViewVB,
	onBindView: ArrayAdapter<ItemT>.(
		binding: ViewVB,
		item: ItemT,
		position: Int,
	) -> Unit,
	onBindDropDownView: ArrayAdapter<ItemT>.(
		binding: DropDownViewVB,
		item: ItemT,
		position: Int,
	) -> Unit,
): ArrayAdapter<ItemT> = SimpleSingleItemArrayAdapter(
	context = context,
	inflateView = inflateView,
	inflateDropDownView = inflateDropDownView,
	onBindView = onBindView,
	onBindDropDownView = onBindDropDownView,
)


private fun <VB : ViewBinding, ItemT : Any> defaultArrayAdapterBind(binding: VB, item: ItemT) {

	val textView: TextView? = when (val root = binding.root) {
		is TextView  -> root
		is ViewGroup -> {
			val descendants = root.descendants

			val textViewWithId = descendants.firstOrNull { view ->
				view is TextView && view.id != -1
			} as? TextView

			textViewWithId ?: descendants.firstOrNull { view ->
				view is TextView
			} as? TextView
		}
		else         -> null
	}

	textView?.text = when (item) {
		is CharSequence -> item
		else            -> "$item"
	}
}