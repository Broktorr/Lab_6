package com.example.jetcompous

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.ui.Modifier
import androidx.navigation.compose.composable
import androidx.navigation.compose.NavHost
import coil.compose.rememberImagePainter
import androidx.compose.material.Button
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import android.content.Context
import androidx.compose.material.icons.filled.Edit
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.room.Database
import androidx.room.OnConflictStrategy
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Entity
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val photoUrl: String,
    val description: String
)
@Dao
interface UserDao {
    @Query("SELECT * FROM User")
    fun getAll(): Flow<List<User>>
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user: User)
    @Delete
    suspend fun delete(user: User)
    @Update
    suspend fun update(user: User)
}
@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
class UserViewModel(application: Application): AndroidViewModel(application) {
    private val userDao = AppDatabase.getDatabase(application).userDao()
    val users = userDao.getAll().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    private val _selectedUser = MutableStateFlow<User?>(null)
    val selectedUser: StateFlow<User?> get() = _selectedUser
    init {
        viewModelScope.launch {
            val currentUsers = userDao.getAll().first()
            if (currentUsers.isEmpty()) {
                userDao.insert(User(name = "John Doe", photoUrl = "https://i.etsystatic.com/39530894/r/il/a067d2/4562558346/il_fullxfull.4562558346_g7qx.jpg", description = "John is a software engineer with a passion for coding."))
                userDao.insert(User(name = "Jane Smith", photoUrl = "https://cs12.pikabu.ru/post_img/big/2022/04/09/9/1649513670117830723.png", description = "Jane is an artist who loves expressing her creativity through various mediums."))
                userDao.insert(User(name= "David Johnson", photoUrl = "https://i.pinimg.com/originals/3d/4f/c5/3d4fc5f11456a65949962360fddba5f8.jpg", description = "David is a photographer who captures breathtaking moments through his lens."))
                userDao.insert(User(name= "Emily Brown", photoUrl = "https://images-wixmp-ed30a86b8c4ca887773594c2.wixmp.com/f/e8d9c01b-ae96-44c2-82c4-ae1230263a48/ddaw7x4-a44e1f07-36e8-492a-accb-c812dc0b0b32.jpg/v1/fit/w_414,h_316,q_70,strp/aside_justice_there_is_evil_by_skunkyfly_ddaw7x4-414w.jpg?token=eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ1cm46YXBwOjdlMGQxODg5ODIyNjQzNzNhNWYwZDQxNWVhMGQyNmUwIiwiaXNzIjoidXJuOmFwcDo3ZTBkMTg4OTgyMjY0MzczYTVmMGQ0MTVlYTBkMjZlMCIsIm9iaiI6W1t7ImhlaWdodCI6Ijw9NzgxIiwicGF0aCI6IlwvZlwvZThkOWMwMWItYWU5Ni00NGMyLTgyYzQtYWUxMjMwMjYzYTQ4XC9kZGF3N3g0LWE0NGUxZjA3LTM2ZTgtNDkyYS1hY2NiLWM4MTJkYzBiMGIzMi5qcGciLCJ3aWR0aCI6Ijw9MTAyNCJ9XV0sImF1ZCI6WyJ1cm46c2VydmljZTppbWFnZS5vcGVyYXRpb25zIl19.4gRbU9BgL3B5u969_GYoEAQd7nEib2okrPaRMairciA", description = "Emily is a fashion designer who creates unique and stylish clothing."))
                userDao.insert(User(name= "Michael Johnson", photoUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT3cS1QBNlJe7tZnI69xtX7oxqxakReidrwHQ&usqp=CAU", description = "Michael is a musician who plays multiple instruments and enjoys composing his own music."))
                userDao.insert(User(name= "Sarah Davis", photoUrl = "https://w0.peakpx.com/wallpaper/683/656/HD-wallpaper-kawaii-anime-boy-for-android-anime-boy-love-anime-boy.jpg", description = "Sarah is a book lover and an avid reader who enjoys exploring different genres."))
                userDao.insert(User(name= "Thomas Wilson", photoUrl = "https://pbs.twimg.com/media/FQSuxplXsAITFcN?format=jpg&name=medium", description = "Thomas is a fitness enthusiast who believes in leading a healthy and active lifestyle."))
                userDao.insert(User(name= "Olivia Martinez", photoUrl = "https://c4.wallpaperflare.com/wallpaper/701/379/747/anime-girls-anime-original-characters-cat-ears-pink-hair-hd-wallpaper-preview.jpg", description = "Olivia is a nature lover and enjoys spending time outdoors, exploring new hiking trails."))
                userDao.insert(User(name= "James Taylor", photoUrl = "https://i.pinimg.com/originals/1b/90/bb/1b90bb4cf8b58fcc23e96f6a228f5ea3.png", description = "James is a chef who specializes in creating exquisite culinary delights."))
                userDao.insert(User(name= "Emma Anderson", photoUrl = "https://www.pngkey.com/png/detail/180-1804708_beautiful-anime-girl-manga-anime-anime-art-zero.png", description = "Emma is a travel blogger who documents her adventures and shares her experiences with her audience."))
                userDao.insert(User(name= "William Lee", photoUrl = "https://lunarmimi.net/wp-content/uploads/2022/11/Creative-NAI-Prompts-14.webp", description = "William is a scientist who conducts research in the field of renewable energy."))
                userDao.insert(User(name= "Sophia Clark", photoUrl = "https://blogger.googleusercontent.com/img/b/R29vZ2xl/AVvXsEgBe-dBpjmQZrMjlAqya3bWutL7CxeKq8km_PwRJgv7snKqjBDxZ9PTDnCnvV5Wuv4qvXFT2yIoXz-Rljdp5DeHVjUfdwgpTFxugzfLe_oACFDA-jPTZZpjhwLwVrNd-nMvaYTaS3N1scZiU1zAQ1K0YCJ96-xjkItGNBU6Gg6dT4hkonXt6Gn80ryXRg/w1600/anime-cyberpunk-female-soldier-anime-art-ponytail-thumb.webp", description = "Sophia is an entrepreneur who owns a successful online business."))
                userDao.insert(User(name= "Daniel Rodriguez", photoUrl = "https://static.displate.com/280x392/displate/2023-01-19/16e24cb80be2de1ab2fedbf992fba9c4_ad1da4132e8b30cf4fdb5517fb34fd8c.jpg", description = "Daniel is a graphic designer who brings creativity and innovation to his designs."))
                userDao.insert(User(name= "Ava Scott", photoUrl = "https://animesher.com/orig/0/85/855/8550/animesher.com_magic-girl-anime-girl-anime-art-855069.png", description = "Ava is a teacher who is passionate about inspiring and educating young minds."))
                userDao.insert(User(name= "Josephine Young", photoUrl = "https://a-static.besthdwallpaper.com/chainsaw-man-reze-wallpaper-2560x1440-79296_51.jpg", description = "Josephine is a social worker who is dedicated to making a positive impact in her community."))
                userDao.insert(User(name= "Henry Turner", photoUrl = "https://w0.peakpx.com/wallpaper/626/259/HD-wallpaper-anime-girl-art-beautiful-hot-kiz-ninja-pink-sexy.jpg", description = "Henry is a sports enthusiast and enjoys playing basketball and soccer."))
                userDao.insert(User(name= "Chloe Hill", photoUrl = "https://i.pinimg.com/originals/80/d3/ef/80d3efa0bb101ceccbc9191378b33313.jpg", description = "Chloe is an animal lover and volunteers at a local shelter to help animals in need."))
                userDao.insert(User(name= "Benjamin Baker", photoUrl = "https://p4.wallpaperbetter.com/wallpaper/115/284/991/anime-girls-anime-kyrie-meii-wallpaper-preview.jpg", description = "Benjamin is a journalist who investigates and reports on current events and important issues."))
                userDao.insert(User(name= "Mia Phillips", photoUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTZV4-47gNPFbeasQYvUV5Q5w-rKEI6roGVjpw4JATb-zSgGdSKn4509mw-80i96FUStjk&usqp=CAU", description = "Mia is an architect who designs innovative and sustainable buildings."))
                userDao.insert(User(name= "Samuel Morris", photoUrl = "https://images4.alphacoders.com/119/1193618.jpg", description = "Samuel is a firefighter who bravely serves his community and helps keep people safe."))
                userDao.insert(User(name= "Natalie Mitchell", photoUrl = "https://foni.club/uploads/posts/2023-03/1677665937_foni-club-p-skelet-anime-art-15.jpg", description = "Natalie is a veterinarian who cares for animals and ensures their well-being."))
                userDao.insert(User(name= "Andrew Nelson", photoUrl = "https://animemotivation.com/wp-content/uploads/2018/06/rock-and-revy-rebecca.jpg", description = "Andrew is a financial advisor who helps individuals and businesses plan for their financial future."))
                userDao.insert(User(name= "Grace Rivera", photoUrl = "https://t3.ftcdn.net/jpg/04/49/20/06/360_F_449200631_gq7SnaFI60z6RetiiyMwHzcwKKIiQoHu.jpg", description = "Grace is an artist who expresses her emotions and ideas through her beautiful paintings."))
                userDao.insert(User(name= "Jack Edwards", photoUrl = "https://i.pinimg.com/236x/25/db/4e/25db4ea11bd30c16236e2d7b18ecb308.jpg", description = "Jack is a software developer who enjoys creating innovative applications."))
                userDao.insert(User(name= "Lily Stewart", photoUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRxMeM5UgfZXrVucbAc3EJplwIhWA09vCxnuA&usqp=CAU", description = "Lily is a fashion blogger who shares her style tips and fashion inspiration with her followers."))
                userDao.insert(User(name= "Christopher Collins", photoUrl = "https://cdn.wallpapersafari.com/10/95/WEzrTl.jpg", description = "Christopher is a filmmaker who tells captivating stories through his movies."))
                userDao.insert(User(name= "Ella Murphy", photoUrl = "https://foni.club/uploads/posts/2023-02/1677256401_foni-club-p-devushka-anime-art-na-avu-7.jpg", description = "Ella is a dancer who is passionate about expressing herself through the art of dance."))
                userDao.insert(User(name= "Daniel Cooper", photoUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRNNrWYnl5rpmar9gGDvXiMZZ77kZptIQG_pdEkCVSCAfU4e1q62ommBTUs-FMlWjvXG8E&usqp=CAU", description = "Daniel is a teacher who is dedicated to providing quality education to his students."))
                userDao.insert(User(name= "Victoria Reed", photoUrl = "https://i.pinimg.com/736x/75/dc/73/75dc73306e03f7b8ad6b29c40a43ab59.jpg", description = "Victoria is an environmentalist who works to protect and conserve the natural environment."))
                userDao.insert(User(name= "Maxwell Ross", photoUrl = "https://wallpapers.com/images/featured/anime-art-zgganwozkxu1fx9d.jpg", description = "Maxwell is a pilot who enjoys soaring through the skies and exploring new destinations."))
            }
        }
    }
    fun onUserSelected(user: User) = viewModelScope.launch {
        _selectedUser.emit(user)
    }
    fun deleteUser(user: User) = viewModelScope.launch {
        userDao.delete(user)
    }
    fun saveUser(name: String, description: String, photoUrl: String) = viewModelScope.launch {
        userDao.insert(User(name = name, description = description, photoUrl = photoUrl))
    }
    fun updateUser(user: User) = viewModelScope.launch {
        userDao.update(user)
    }
}
@Composable
fun UserRow(user: User, viewModel: UserViewModel, onUserClick: (User) -> Unit, onEditClick: (User) -> Unit) {
    val displayName = if (user.name.length > 14) {
        user.name.take(14) + "..."
    } else {
        user.name
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { onUserClick(user) }
    ) {
        Image(
            painter = rememberImagePainter(data = user.photoUrl),
            contentDescription = null,
            modifier = Modifier
                .size(100.dp)
                .padding(end = 8.dp)
        )
        Text(displayName, style = MaterialTheme.typography.h5.copy(fontSize = 15.sp), color = Color.White)

        Spacer(Modifier.weight(1f))

        // Edit button
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = "Edit User",
            modifier = Modifier
                .clickable { onEditClick(user) }
                .padding(8.dp)
                .size(40.dp),
            tint = Color.Yellow
        )
        // Delete button
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = "Delete User",
            modifier = Modifier
                .clickable { viewModel.deleteUser(user) }
                .padding(8.dp)
                .size(40.dp),
            tint = Color.Red
        )

    }
}

@Composable
fun UserListScreen(users: List<User>, onUserClick: (User) -> Unit, onAddClick: () -> Unit, onEditClick: (User) -> Unit, viewModel: UserViewModel) {
    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        LazyColumn {
            items(users) { user ->
                UserRow(user, viewModel, onUserClick, onEditClick)
            }
        }
        FloatingActionButton(
            onClick = onAddClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            backgroundColor = MaterialTheme.colors.primary,
            contentColor = MaterialTheme.colors.onPrimary,
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add User")
        }
    }
}

@Composable
fun MyApp(userViewModel: UserViewModel, navController: NavController) {
    val users by userViewModel.users.collectAsState()
    NavHost(navController as NavHostController, startDestination = "user_list") {
        composable("user_list") {
            UserListScreen(users, { user ->
                userViewModel.onUserSelected(user)
                navController.navigate("user_profile")
            }, {
                navController.navigate("add_user")
            }, { user ->
                userViewModel.onUserSelected(user)
                navController.navigate("edit_user")
            }, userViewModel)
        }
        composable("add_user") {
            AddUserScreen(userViewModel) {
                navController.navigateUp()
            }
        }
        composable("edit_user") {
            val selectedUser by userViewModel.selectedUser.collectAsState()
            selectedUser?.let { user ->
                EditUserScreen(userViewModel, user) {
                    navController.navigateUp()
                }
            }
        }
        composable("user_profile") {
            val selectedUser by userViewModel.selectedUser.collectAsState()
            selectedUser?.let { user ->
                UserProfileScreen(user) {
                    navController.navigateUp()
                }
            }
        }
    }
}

@Composable
fun UserProfileScreen(user: User?, onBackClick: () -> Unit) {
    user?.let {
    val isExpanded = remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Image(
            painter = rememberImagePainter(data = user.photoUrl),
            contentDescription = null,
            modifier = Modifier
                .height(if (isExpanded.value) 800.dp else 400.dp)
                .fillMaxWidth()
                .clickable { isExpanded.value = !isExpanded.value }
        )
        Spacer(modifier = Modifier.height(16.dp))

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(text = "Name: " + user.name, style = MaterialTheme.typography.h5, color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "About me: " + user.description, style = MaterialTheme.typography.body1, color = Color.White)
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onBackClick) {
                Text("Go back")
            }
        }
    }
}}
@Composable
fun AddUserScreen(viewModel: UserViewModel, onBackClick: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var photoUrl by remember { mutableStateOf("") }
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(text = "Name", style = MaterialTheme.typography.h5, color = Color.White)
        TextField(value = name, onValueChange = { name = it })
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Description", style = MaterialTheme.typography.h5, color = Color.White)
        TextField(value = description, onValueChange = { description = it })
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Photo URL", style = MaterialTheme.typography.h5, color = Color.White)
        TextField(value = photoUrl, onValueChange = { photoUrl = it })
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = {
            viewModel.saveUser(name, description, photoUrl)
            onBackClick()
        }) {
            Text("Save")
        }
    }
}
@Composable
fun EditUserScreen(viewModel: UserViewModel, user: User, onBackClick: () -> Unit) {
    var name by remember { mutableStateOf(user.name) }
    var description by remember { mutableStateOf(user.description) }
    var photoUrl by remember { mutableStateOf(user.photoUrl) }
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(text = "Name", style = MaterialTheme.typography.h5, color = Color.White)
        TextField(value = name, onValueChange = { name = it })
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Description", style = MaterialTheme.typography.h5, color = Color.White)
        TextField(value = description, onValueChange = { description = it })
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Photo URL", style = MaterialTheme.typography.h5, color = Color.White)
        TextField(value = photoUrl, onValueChange = { photoUrl = it })
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = {
            viewModel.updateUser(User(user.id, name, photoUrl, description))
            onBackClick()
        }) {
            Text("Save")
        }
    }
}
