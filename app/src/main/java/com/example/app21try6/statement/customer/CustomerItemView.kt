package com.example.app21try6.statement.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.app21try6.R
import com.example.app21try6.database.tables.CustomerTable
import com.example.app21try6.statement.CustomerDelListener
import com.example.app21try6.statement.CustomerListener

@Composable
fun CustomerItemView(
    item: CustomerTable,
    clickListener: CustomerListener,
    deleteListener: CustomerDelListener
)
{
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(2.dp)
            ,colors = CardDefaults.cardColors(
            containerColor =Color.Transparent

        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)

    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Transparent)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.Transparent)
                    .padding(12.dp), // Add padding so text doesn't touch the edges

                horizontalAlignment = Alignment.Start
            ) {
                // Header: Word and Definition
                Text(
                    modifier = Modifier,
                    text = "${item.customerBussinessName}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = colorResource(id = R.color.mainTextColor)
                    )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    modifier = Modifier,
                    text = "${item.customerAddress}",
                    style = MaterialTheme.typography.titleSmall,
                    color = colorResource(id = R.color.mainTextColor)
                )
                Text(
                    modifier = Modifier,
                    text = "${item.customerLocation}",
                    style = MaterialTheme.typography.titleSmall,
                    color = colorResource(id = R.color.mainTextColor)
                )

            }

// ... rest of imports

            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd),
                horizontalArrangement = Arrangement.spacedBy((-8).dp), // Negative spacing pulls them together
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick ={ clickListener.onClick(item) },
                    modifier = Modifier.size(40.dp) // Shrinks the container from 48dp to 40dp
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = Color.Gray,
                        modifier = Modifier.size(25.dp) // Scales the icon appropriately
                    )
                }

                IconButton(
                    onClick = { deleteListener.onClick(item) },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.Gray,
                        modifier = Modifier.size(25.dp)
                    )
                }
            }
        }
    }

}