package com.example.simplearrayadapter.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.view.descendants
import androidx.viewbinding.ViewBinding
import kotlin.reflect.KClass

private class SimpleMultiItemArrayAdapter<ItemT : Any>(
	context: Context,
	itemBindings: List<BindingData<ItemT>>,
) : ArrayAdapter<ItemT>(context, 0) {

	private val bindings = itemBindings.distinct()
	private val itemClassToViewType = itemBindings.mapIndexed { index, data -> data.itemClass to index }.toMap()


	override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

		val layoutInflater = LayoutInflater.from(parent.context)

		val viewType = getItemViewType(position)
		val itemBindingData = bindings[viewType]
		val binding = convertView?.tag as? ViewBinding ?: itemBindingData.inflateView(layoutInflater, parent, false)
		val item = getItem(position)!!

		itemBindingData.onBindView(this, binding, item, position)

		return binding.root
	}

	override fun getItemViewType(position: Int): Int {

		val item = getItem(position)!!

		return itemClassToViewType[item::class]!!
	}

	override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {

		val layoutInflater = LayoutInflater.from(parent.context)

		val item = getItem(position)!!
		val viewType = getItemViewType(position)
		val itemBindingData = bindings[viewType]

		val binding = if (itemBindingData.inflateDropDownView == null) {

			val binding = convertView?.tag as? ViewBinding ?: itemBindingData.inflateView(layoutInflater, parent, false).apply { root.tag = this }

			if (itemBindingData.onBindDropDownView != null) {

				itemBindingData.onBindDropDownView.invoke(this, binding, item, position)

				binding
			}
			else {
				itemBindingData.onBindView(this, binding, item, position)

				binding
			}
		}
		else {
			if (itemBindingData.onBindDropDownView != null) {
				val binding = convertView?.tag as? ViewBinding ?: itemBindingData.inflateDropDownView.invoke(layoutInflater, parent, false).apply { root.tag = this }

				itemBindingData.onBindDropDownView.invoke(this, binding, item, position)

				binding
			}
			else {
				val binding = convertView?.tag as? ViewBinding ?: itemBindingData.inflateView.invoke(layoutInflater, parent, false).apply { root.tag = this }

				itemBindingData.onBindView(this, binding, item, position)

				binding
			}
		}

		return binding.root
	}
}


@Suppress("FunctionName")
fun <ItemT : Any> SimpleArrayAdapter(
	context: Context,
	itemBindings: List<BindingData<ItemT>>,
): ArrayAdapter<ItemT> = SimpleMultiItemArrayAdapter(
	context = context,
	itemBindings = itemBindings,
)


@Suppress("FunctionName")
fun <VB : ViewBinding> TextBinding(
	inflateView: (LayoutInflater, ViewGroup, Boolean) -> VB,
): BindingData<String> {
	return BindingData(
		itemClass = String::class,
		inflateView = inflateView,
	)
}

@Suppress("FunctionName", "UNCHECKED_CAST")
inline fun <VB : ViewBinding, reified ItemT : Any> Binding(
	noinline inflateView: (LayoutInflater, ViewGroup, Boolean) -> VB,
	noinline onBindView: ArrayAdapter<ItemT>.(
		binding: VB,
		item: ItemT,
		position: Int,
	) -> Unit,
): BindingData<ItemT> {
	return BindingData(
		itemClass = ItemT::class,
		inflateView = inflateView,
		onBindView = onBindView as ArrayAdapter<ItemT>.(
			binding: ViewBinding,
			item: ItemT,
			position: Int,
		) -> Unit,
	)
}

@Suppress("FunctionName", "UNCHECKED_CAST")
inline fun <VB : ViewBinding, reified ItemT : Any> Binding(
	noinline inflateView: (LayoutInflater, ViewGroup, Boolean) -> VB,
	noinline onBindView: ArrayAdapter<ItemT>.(
		binding: VB,
		item: ItemT,
		position: Int,
	) -> Unit,
	noinline onBindDropDownView: ArrayAdapter<ItemT>.(
		binding: VB,
		item: ItemT,
		position: Int,
	) -> Unit,
): BindingData<ItemT> {
	return BindingData(
		itemClass = ItemT::class,
		inflateView = inflateView,
		onBindView = onBindView as ArrayAdapter<ItemT>.(
			binding: ViewBinding,
			item: ItemT,
			position: Int,
		) -> Unit,
		onBindDropDownView = onBindDropDownView as ArrayAdapter<ItemT>.(
			binding: ViewBinding,
			item: ItemT,
			position: Int,
		) -> Unit,
	)
}

@Suppress("FunctionName", "UNCHECKED_CAST")
inline fun <ViewVB : ViewBinding, DropDownViewVB : ViewBinding, reified ItemT : Any> Binding(
	noinline inflateView: (LayoutInflater, ViewGroup, Boolean) -> ViewVB,
	noinline onBindView: ArrayAdapter<ItemT>.(
		binding: ViewVB,
		item: ItemT,
		position: Int,
	) -> Unit,
	noinline inflateDropDownView: ((LayoutInflater, ViewGroup, Boolean) -> DropDownViewVB),
	noinline onBindDropDownView: (ArrayAdapter<ItemT>.(
		binding: DropDownViewVB,
		item: ItemT,
		position: Int,
	) -> Unit)? = null,
): BindingData<ItemT> {
	return BindingData(
		itemClass = ItemT::class,
		inflateView = inflateView,
		onBindView = onBindView as ArrayAdapter<ItemT>.(
			binding: ViewBinding,
			item: ItemT,
			position: Int,
		) -> Unit,
		inflateDropDownView = inflateDropDownView,
		onBindDropDownView = onBindDropDownView as ArrayAdapter<ItemT>.(
			binding: ViewBinding,
			item: ItemT,
			position: Int,
		) -> Unit,
	)
}


class BindingData<out ItemT : Any>(
	val itemClass: KClass<@UnsafeVariance ItemT>,
	val inflateView: (LayoutInflater, ViewGroup, Boolean) -> ViewBinding,
	val onBindView: ArrayAdapter<@UnsafeVariance ItemT>.(
		binding: ViewBinding,
		item: @UnsafeVariance ItemT,
		position: Int,
	) -> Unit = { binding, item, _ ->
		defaultArrayAdapterBind(binding, item)
	},
	val inflateDropDownView: ((LayoutInflater, ViewGroup, Boolean) -> ViewBinding)? = null,
	val onBindDropDownView: (ArrayAdapter<@UnsafeVariance ItemT>.(
		binding: ViewBinding,
		item: @UnsafeVariance ItemT,
		position: Int,
	) -> Unit)? = null,
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