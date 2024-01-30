package cd.infoset.smartpay.client.screens.singup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cd.infoset.smaprtpay.merchant.ui.theme.FlexPayTheme
import cd.infoset.smaprtpay.merchant.ui.theme.SingUpTextField
import cd.infoset.smaprtpay.merchant.ui.theme.darkOrange


enum class AccountType {
    INDIVIDUAL,
    BUSINESS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SignUpScreen(
    onPopBackStack: () -> Unit,
) {

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    var firstname by rememberSaveable {
        mutableStateOf("")
    }
    var lastname by rememberSaveable {
        mutableStateOf("")
    }
    var phoneOne by rememberSaveable {
        mutableStateOf("")
    }
    var phoneTwo by rememberSaveable {
        mutableStateOf("")
    }
    var street by rememberSaveable {
        mutableStateOf("")
    }
    var zipCode by rememberSaveable {
        mutableStateOf("")
    }
    var accountType by rememberSaveable {
        mutableStateOf(AccountType.INDIVIDUAL)
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text(text = "Sign up") },
                navigationIcon = {
                    IconButton(onClick = onPopBackStack) {
                        Icon(imageVector = Icons.Rounded.ArrowBack, contentDescription = null)
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(vertical = 8.dp, horizontal = 16.dp)
        ) {
            SingUpTextField(
                value = firstname,
                onValueChange = { firstname = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(text = "Firstname") }
            )

            SingUpTextField(
                value = lastname,
                onValueChange = { lastname = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(text = "Lastname") }
            )

            SingUpTextField(
                value = "",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(text = "Gender") },
                trailingIcon = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            imageVector = Icons.Rounded.ExpandMore,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )

            SingUpTextField(
                value = phoneOne,
                onValueChange = { phoneOne = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(text = "Phone 1") }
            )

            SingUpTextField(
                value = phoneTwo,
                onValueChange = { phoneTwo = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(text = "Phone 2") }
            )

            Spacer(modifier = Modifier.height(40.dp))
            Text(
                text = "Address",
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            SingUpTextField(
                value = street,
                onValueChange = { street = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(text = "Street") }
            )

            SingUpTextField(
                value = "",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(text = "City") }
            )

            SingUpTextField(
                value = zipCode,
                onValueChange = { zipCode = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(text = "Zip code") }
            )

            SingUpTextField(
                value = "",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(text = "Province/State") },
                trailingIcon = {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            imageVector = Icons.Rounded.ExpandMore,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )

            SingUpTextField(
                value = "",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(text = "Country") }
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(start = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(alignment = Alignment.Center),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Usage", color = MaterialTheme.colorScheme.primary)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.selectableGroup()
                    ) {
                        RadioButton(
                            selected = accountType == AccountType.INDIVIDUAL,
                            onClick = { accountType = AccountType.INDIVIDUAL },
                            colors = RadioButtonDefaults.colors(
                                unselectedColor = MaterialTheme.colorScheme.primary
                            )
                        )
                        Text(
                            text = "Individual",
                            color = MaterialTheme.colorScheme.primary
                        )

                        RadioButton(
                            selected = accountType == AccountType.BUSINESS,
                            onClick = { accountType = AccountType.BUSINESS },
                            colors = RadioButtonDefaults.colors(
                                unselectedColor = MaterialTheme.colorScheme.primary
                            )
                        )
                        Text(
                            text = "Business",
                            color = MaterialTheme.colorScheme.primary
                        )

                    }
                }
            }
            Divider(
                color = MaterialTheme.colorScheme.primary
            )

            SingUpTextField(
                value = "",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(text = "Username") }
            )

            SingUpTextField(
                value = "",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(text = "Password") },
                visualTransformation = PasswordVisualTransformation()
            )

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "I agree to the")
                Column {
                    TextButton(onClick = { /*TODO*/ }) {
                        Text(text = "Terms & Conditions")
                    }
                }
            }

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = { /*TODO*/ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = darkOrange
                    )
                ) {
                    Text(text = "Next")
                }
            }
        }
    }
}

@Preview
@Composable
private fun SignUpScreenPreview() {
    FlexPayTheme {
        SignUpScreen(onPopBackStack = {})
    }
}