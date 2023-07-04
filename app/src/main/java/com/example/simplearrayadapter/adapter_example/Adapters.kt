package com.example.simplearrayadapter.adapter_example

import android.content.Context
import com.example.simplearrayadapter.adapter.Binding
import com.example.simplearrayadapter.adapter.SimpleArrayAdapter
import com.example.simplearrayadapter.adapter.TextBinding
import com.example.simplearrayadapter.databinding.ItemPhoneCodeBinding
import com.example.simplearrayadapter.model.PhoneCodeInfo

@Suppress("FunctionName")
fun PhoneCodeArrayAdapter(context: Context) = SimpleArrayAdapter(
	context = context,
	inflateView = ItemPhoneCodeBinding::inflate
) { binding, item: PhoneCodeInfo, _ ->

	binding.apply {
		tvPhoneCode.text = item.phoneCode
		tvCountryName.text = item.countryName
	}
}

@Suppress("FunctionName")
fun PhoneCodeArrayAdapter2(context: Context) = SimpleArrayAdapter(
	context = context,
	inflateView = ItemPhoneCodeBinding::inflate,
	onBindView = { binding, item: PhoneCodeInfo, _ ->

		binding.apply {
			tvPhoneCode.text = item.phoneCode
			tvCountryName.text = item.countryName
		}
	},
	onBindDropDownView = { binding, item: PhoneCodeInfo, _ ->

		binding.apply {
			tvPhoneCode.text = item.countryName
			tvCountryName.text = item.phoneCode
		}
	},
)

@Suppress("FunctionName")
fun SomeArrayAdapter(context: Context) = SimpleArrayAdapter(
	context = context,
	itemBindings = listOf(
		TextBinding(ItemPhoneCodeBinding::inflate),
		Binding(ItemPhoneCodeBinding::inflate) { binding, item: PhoneCodeInfo, _ ->

		},
		Binding(
			inflateView = ItemPhoneCodeBinding::inflate,
			onBindView = { binding, item: ItemOne, _ ->

			},
			onBindDropDownView = { binding, item: ItemOne, _ ->

			},
		),
		Binding(
			inflateView = ItemPhoneCodeBinding::inflate,
			inflateDropDownView = ItemPhoneCodeBinding::inflate,
			onBindView = { binding, item: ItemTwo, _ ->

			},
			onBindDropDownView = { binding, item: ItemTwo, _ ->

			},
		),
	),
)

sealed class MultiItem
data class ItemOne(val someValue: String) : MultiItem()
data class ItemTwo(val someOtherValue: String) : MultiItem()