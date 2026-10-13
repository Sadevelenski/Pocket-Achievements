@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
package com.juan.achievements

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.juan.achievements.data.AchievementEntity
import com.juan.achievements.data.AppDatabase
import com.juan.achievements.data.CategoryEntity
import com.juan.achievements.data.Rarity
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    AchievementsApp()
                }
            }
        }
    }
}

@Composable
private fun AchievementsApp() {
    val context = LocalContext.current
    val database = remember { AppDatabase.getInstance(context) }
    val categories by database.categoryDao().observeAll().collectAsStateWithLifecycle(initialValue = emptyList())
    val scope = rememberCoroutineScope()
    var selectedCategory by remember { mutableStateOf<CategoryEntity?>(null) }

    if (selectedCategory == null) {
        CategoryScreen(
            categories = categories,
            onOpen = { selectedCategory = it },
            onAdd = { name, description ->
                val order = categories.size
                scope.launch {
                    database.categoryDao().insert(CategoryEntity(name = name, description = description, sortOrder = order))
                }
            },
            onDelete = { category ->
                scope.launch {
                    database.categoryDao().delete(category)
                }
            }
        )
    } else {
        AchievementScreen(
            category = selectedCategory!!,
            database = database,
            onBack = { selectedCategory = null }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryScreen(
    categories: List<CategoryEntity>,
    onOpen: (CategoryEntity) -> Unit,
    onAdd: (String, String) -> Unit,
    onDelete: (CategoryEntity) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var sortBy by remember { mutableStateOf("Personalizado") }
    val sortedCategories = when (sortBy) {
        "Nombre A-Z" -> categories.sortedBy { it.name.lowercase() }
        "Nombre Z-A" -> categories.sortedByDescending { it.name.lowercase() }
        "Más recientes" -> categories.sortedByDescending { it.createdAt }
        "Última actualización" -> categories.sortedByDescending { it.updatedAt }
        else -> categories.sortedBy { it.sortOrder }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Column {
                    Text("Achievements", fontWeight = FontWeight.Bold)
                    Text("Tus objetivos, a tu manera", style = MaterialTheme.typography.labelMedium)
                }
            })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Crear categoría")
            }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
            Text("Categorías", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(top = 8.dp, bottom = 8.dp))
            SortDropdown(
                label = "Ordenar categorías",
                selected = sortBy,
                options = listOf("Personalizado", "Nombre A-Z", "Nombre Z-A", "Más recientes", "Última actualización"),
                onSelected = { sortBy = it }
            )
            if (sortedCategories.isEmpty()) {
                EmptyState(
                    title = "Todavía no hay categorías",
                    body = "Creá una categoría para empezar a organizar tus logros."
                )
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(top = 12.dp)) {
                    items(sortedCategories, key = { it.id }) { category ->
                        Card(
                            modifier = Modifier.fillMaxWidth().clickable { onOpen(category) },
                            shape = RoundedCornerShape(18.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 14.dp, bottom = 14.dp, end = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(Modifier.weight(1f)) {
                                    Text(category.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                                    if (category.description.isNotBlank()) {
                                        Text(category.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Text("Abrir categoría  ›", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 4.dp))
                                }
                                IconButton(onClick = { onDelete(category) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar categoría")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddTextDialog(
            title = "Nueva categoría",
            nameLabel = "Nombre de la categoría",
            descriptionLabel = "Descripción (opcional)",
            onDismiss = { showAddDialog = false },
            onSave = { name, description ->
                if (name.isNotBlank()) onAdd(name.trim(), description.trim())
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun AchievementScreen(
    category: CategoryEntity,
    database: AppDatabase,
    onBack: () -> Unit
) {
    val achievements by database.achievementDao().observeForCategory(category.id).collectAsStateWithLifecycle(initialValue = emptyList())
    val scope = rememberCoroutineScope()
    var showAddDialog by remember { mutableStateOf(false) }
    var sortBy by remember { mutableStateOf("Personalizado") }
    val sortedAchievements = when (sortBy) {
        "Nombre A-Z" -> achievements.sortedBy { it.name.lowercase() }
        "Nombre Z-A" -> achievements.sortedByDescending { it.name.lowercase() }
        "Prestigio" -> achievements.sortedBy { rarityPrestige(it.rarity) }
        "Obtenidos primero" -> achievements.sortedByDescending { it.isObtained }
        "Última actualización" -> achievements.sortedByDescending { it.updatedAt }
        else -> achievements.sortedBy { it.sortOrder }
    }
    val obtainedCount = achievements.count { it.isObtained }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(category.name) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Crear logro")
            }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
            Text("$obtainedCount de ${achievements.size} obtenidos", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp))
            SortDropdown(
                label = "Ordenar logros",
                selected = sortBy,
                options = listOf("Personalizado", "Nombre A-Z", "Nombre Z-A", "Prestigio", "Obtenidos primero", "Última actualización"),
                onSelected = { sortBy = it }
            )
            if (sortedAchievements.isEmpty()) {
                EmptyState("Todavía no hay logros", "Creá tu primer logro con el botón +.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.padding(top = 12.dp)) {
                    items(sortedAchievements, key = { it.id }) { achievement ->
                        AchievementCard(
                            achievement = achievement,
                            onToggle = {
                                scope.launch {
                                    database.achievementDao().update(
                                        achievement.copy(isObtained = !achievement.isObtained, updatedAt = System.currentTimeMillis())
                                    )
                                }
                            },
                            onDelete = {
                                scope.launch { database.achievementDao().delete(achievement) }
                            }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddAchievementDialog(
            onDismiss = { showAddDialog = false },
            onSave = { name, description, rarity ->
                if (name.isNotBlank()) {
                    scope.launch {
                        database.achievementDao().insert(
                            AchievementEntity(
                                categoryId = category.id,
                                name = name.trim(),
                                description = description.trim(),
                                rarity = rarity.name,
                                sortOrder = achievements.size
                            )
                        )
                    }
                }
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun AchievementCard(
    achievement: AchievementEntity,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val rarity = Rarity.entries.firstOrNull { it.name == achievement.rarity } ?: Rarity.BRONZE
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (achievement.isObtained) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 14.dp, top = 12.dp, bottom = 12.dp, end = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(if (achievement.isObtained) rarity.emoji else "🔒", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(end = 12.dp))
            Column(Modifier.weight(1f)) {
                Text(achievement.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text("${rarity.displayName} · ${if (achievement.isObtained) "Obtenido" else "Bloqueado"}", style = MaterialTheme.typography.labelMedium)
                if (achievement.description.isNotBlank()) {
                    Text(achievement.description, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 4.dp))
                }
                OutlinedButton(onClick = onToggle, modifier = Modifier.padding(top = 8.dp)) {
                    Text(if (achievement.isObtained) "Marcar como bloqueado" else "Marcar como obtenido")
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar logro")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddAchievementDialog(
    onDismiss: () -> Unit,
    onSave: (String, String, Rarity) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var rarity by remember { mutableStateOf(Rarity.BRONZE) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nuevo logro") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nombre") }, singleLine = true)
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Descripción (opcional)") })
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                    OutlinedTextField(
                        value = "${rarity.emoji} ${rarity.displayName}",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Rareza") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        Rarity.entries.forEach { option ->
                            DropdownMenuItem(
                                text = { Text("${option.emoji} ${option.displayName}") },
                                onClick = { rarity = option; expanded = false }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = { Button(onClick = { onSave(name, description, rarity) }) { Text("Crear") } },
        dismissButton = { OutlinedButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@Composable
private fun AddTextDialog(
    title: String,
    nameLabel: String,
    descriptionLabel: String,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text(nameLabel) }, singleLine = true)
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text(descriptionLabel) })
            }
        },
        confirmButton = { Button(onClick = { onSave(name, description) }, enabled = name.isNotBlank()) { Text("Crear") } },
        dismissButton = { OutlinedButton(onClick = onDismiss) { Text("Cancelar") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SortDropdown(
    label: String,
    selected: String,
    options: List<String>,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(text = { Text(option) }, onClick = { onSelected(option); expanded = false })
            }
        }
    }
}

@Composable
private fun EmptyState(title: String, body: String) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 56.dp, start = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Text(body, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

private fun rarityPrestige(rarityName: String): Int =
    Rarity.entries.firstOrNull { it.name == rarityName }?.prestige ?: Int.MAX_VALUE
