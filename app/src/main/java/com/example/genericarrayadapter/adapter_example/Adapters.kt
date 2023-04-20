package com.example.genericarrayadapter.adapter_example

import android.content.Context
import com.example.genericarrayadapter.adapter.Binding
import com.example.genericarrayadapter.adapter.GenericArrayAdapter
import com.example.genericarrayadapter.databinding.ItemPhoneCodeBinding
import com.example.genericarrayadapter.model.PhoneCodeInfo

@Suppress("FunctionName")
fun PhoneCodeArrayAdapter(context: Context) = GenericArrayAdapter(
	context = context,
	inflateView = ItemPhoneCodeBinding::inflate
) { item: PhoneCodeInfo, binding, _ ->

	binding.apply()
	{
		tvPhoneCode.text = item.phoneCode
		tvCountryName.text = item.countryName
	}
}

@Suppress("FunctionName")
fun PhoneCodeArrayAdapter2(context: Context) = GenericArrayAdapter(
	context = context,
	inflateView = ItemPhoneCodeBinding::inflate,
	onBindView = { item: PhoneCodeInfo, binding, _ ->

		binding.apply()
		{
			tvPhoneCode.text = item.phoneCode
			tvCountryName.text = item.countryName
		}
	},
	onBindDropDownView = { item: PhoneCodeInfo, binding, _ ->

		binding.apply()
		{
			tvPhoneCode.text = item.countryName
			tvCountryName.text = item.phoneCode
		}
	},
)

@Suppress("FunctionName")
fun SomeArrayAdapter(context: Context) = GenericArrayAdapter(
	context = context,
	itemBindings = listOf(
		Binding(ItemPhoneCodeBinding::inflate) { item: PhoneCodeInfo, binding ->

		}
	)
)