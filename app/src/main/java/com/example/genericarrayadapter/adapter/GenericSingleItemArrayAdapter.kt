package com.example.genericarrayadapter.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.annotation.ArrayRes
import androidx.core.view.descendants
import androidx.viewbinding.ViewBinding

private class GenericSingleItemArrayAdapter
<
	out ItemT : Any,
	ViewVB : ViewBinding,
	DropDownViewVB : ViewBinding,
	>(
	context: Context,
	private val inflateDropDownView: ((LayoutInflater, ViewGroup, Boolean) -> DropDownViewVB)? = null,
	private val onBindDropDownView: (GenericSingleItemArrayAdapter<ItemT, ViewVB, DropDownViewVB>.(
		item: ItemT,
		binding: DropDownViewVB,
		position: Int,
	) -> Unit)? = null,
	private val inflateView: (LayoutInflater, ViewGroup, Boolean) -> ViewVB,
	private val onBindView: GenericSingleItemArrayAdapter<ItemT, ViewVB, DropDownViewVB>.(
		item: ItemT,
		binding: ViewVB,
		position: Int,
	) -> Unit = { item, binding, _ -> defaultBind(item, binding) },
) : ArrayAdapter<@UnsafeVariance ItemT>(context, 0)
{
	override fun getView(position: Int, convertView: View?, parent: ViewGroup): View
	{
		val layoutInflater = LayoutInflater.from(parent.context)

		@Suppress("UNCHECKED_CAST")
		val binding = convertView?.tag as? ViewVB ?: inflateView(layoutInflater, parent, false).apply { root.tag = this }

		val item = getItem(position)!!

		onBindView(item, binding, position)

		return binding.root
	}

	override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View
	{
		val layoutInflater = LayoutInflater.from(parent.context)

		val item = getItem(position)!!

		val binding = if (inflateDropDownView == null)
		{
			if (onBindDropDownView != null)
			{
				@Suppress("UNCHECKED_CAST")
				val binding = convertView?.tag as? DropDownViewVB ?: inflateView(layoutInflater, parent, false).apply { root.tag = this } as DropDownViewVB

				onBindDropDownView.invoke(this, item, binding, position)

				binding
			}
			else
			{
				@Suppress("UNCHECKED_CAST")
				val binding = convertView?.tag as? ViewVB ?: inflateView(layoutInflater, parent, false).apply { root.tag = this }

				onBindView(item, binding, position)

				binding
			}
		}
		else
		{
			if (onBindDropDownView != null)
			{
				@Suppress("UNCHECKED_CAST")
				val binding = convertView?.tag as? DropDownViewVB ?: inflateDropDownView.invoke(layoutInflater, parent, false).apply { root.tag = this }

				onBindDropDownView.invoke(this, item, binding, position)

				binding
			}
			else
			{
				@Suppress("UNCHECKED_CAST")
				val binding = convertView?.tag as? ViewVB ?: inflateView.invoke(layoutInflater, parent, false).apply { root.tag = this }

				onBindView(item, binding, position)

				binding
			}
		}

		return binding.root
	}
}

private fun <ItemT : Any, VB : ViewBinding> defaultBind(item: ItemT, binding: VB)
{
	val textView: TextView? = when (val root = binding.root)
	{
		is TextView  -> root
		is ViewGroup ->
		{
			val descendants = root.descendants

			descendants.find { it is TextView && it.id != -1 } as? TextView ?: descendants.find { it is TextView } as? TextView
		}

		else         -> null
	}

	textView?.text = when (item)
	{
		is CharSequence -> item
		else            -> "$item"
	}
}

@Suppress("FunctionName")
fun <ViewVB : ViewBinding> TextArrayAdapter(
	context: Context,
	inflateView: (LayoutInflater, ViewGroup, Boolean) -> ViewVB,
): ArrayAdapter<String> = GenericSingleItemArrayAdapter<String, ViewVB, Nothing>(
	context = context,
	inflateView = inflateView,
)

@Suppress("FunctionName")
fun <ViewVB : ViewBinding> TextArrayAdapter(
	context: Context,
	@ArrayRes arrayRes: Int,
	inflateView: (LayoutInflater, ViewGroup, Boolean) -> ViewVB,
): ArrayAdapter<CharSequence> = GenericSingleItemArrayAdapter<CharSequence, ViewVB, Nothing>(
	context = context,
	inflateView = inflateView,
).apply()
{
	addAll(context.resources.getTextArray(arrayRes).toList())
}

@Suppress("FunctionName")
fun <ItemT : Any, ViewVB : ViewBinding> GenericArrayAdapter(
	context: Context,
	inflateView: (LayoutInflater, ViewGroup, Boolean) -> ViewVB,
): ArrayAdapter<ItemT> = GenericSingleItemArrayAdapter<ItemT, ViewVB, Nothing>(
	context = context,
	inflateView = inflateView,
)

@Suppress("FunctionName")
fun <ItemT : Any, ViewVB : ViewBinding> GenericArrayAdapter(
	context: Context,
	inflateView: (LayoutInflater, ViewGroup, Boolean) -> ViewVB,
	onBindView: ArrayAdapter<ItemT>.(
		item: ItemT,
		binding: ViewVB,
		position: Int,
	) -> Unit,
): ArrayAdapter<ItemT> = GenericSingleItemArrayAdapter(
	context = context,
	inflateView = inflateView,
	onBindView = onBindView,
	onBindDropDownView = onBindView,
)

@Suppress("FunctionName")
fun <ItemT : Any, ViewVB : ViewBinding> GenericArrayAdapter(
	context: Context,
	inflateView: (LayoutInflater, ViewGroup, Boolean) -> ViewVB,
	onBindDropDownView: ArrayAdapter<ItemT>.(
		item: ItemT,
		binding: ViewVB,
		position: Int,
	) -> Unit,
	onBindView: ArrayAdapter<ItemT>.(
		item: ItemT,
		binding: ViewVB,
		position: Int,
	) -> Unit,
): ArrayAdapter<ItemT> = GenericSingleItemArrayAdapter(
	context = context,
	inflateView = inflateView,
	onBindView = onBindView,
	onBindDropDownView = onBindDropDownView,
)

@Suppress("FunctionName")
fun <ItemT : Any, ViewVB : ViewBinding, DropDownViewVB : ViewBinding> GenericArrayAdapter(
	context: Context,
	inflateView: (LayoutInflater, ViewGroup, Boolean) -> ViewVB,
	inflateDropDownView: (LayoutInflater, ViewGroup, Boolean) -> DropDownViewVB,
	onBindView: ArrayAdapter<ItemT>.(
		item: ItemT,
		binding: ViewVB,
		position: Int,
	) -> Unit,
	onBindDropDownView: ArrayAdapter<ItemT>.(
		item: ItemT,
		binding: DropDownViewVB,
		position: Int,
	) -> Unit,
): ArrayAdapter<ItemT> = GenericSingleItemArrayAdapter(
	context = context,
	inflateView = inflateView,
	inflateDropDownView = inflateDropDownView,
	onBindView = onBindView,
	onBindDropDownView = onBindDropDownView,
)