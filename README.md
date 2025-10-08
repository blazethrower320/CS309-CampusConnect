# Backend

### API Calls
 - Users
    - Get | ``/users`` - Returns a list of all users
    - Get | ``/users/{id}`` > Returns a User with the specific ID
    - Get | ``/users/findUsername/{username}`` - Returns a List<User> with the username
    - Get | ``/users/findUser/{firstName}`` - Returns a User with the firstName
    - Post | ``/users/login`` - With a Body of
        ```json
        {
            "username": "usernameHere",
            "password": "passwordHere"
        }
        ```
        
        Returns: <br>
            ``404`` - User not found <br>
            ``403`` - Password does not match <br>
            ``user`` - If Username and password is correct <br>
    - Get | ``/usernames`` - Returns A list of only Usernames of all Users
    - Get | ``/users/password/{username}`` - Returns the password of the username
    - Patch | `/users/editUsername` - Edits the username of a user id
        ```json
        {
            "newUsername": "newUsername",
            "newPassword": "newPassword"
        }
        ```

        Returns: <br>
            ``true`` - If it was successfully changed <br>
            ``false`` - If the user does not exists <br>
    - Patch | `/users/editPassword` - Edits the password of a user id
        ```json
        {
            "newUsername": "newUsername",
            "newPassword": "newPassword"
        }
        ```

        Returns: <br>
            ``true`` - If it was successfully changed <br>
            ``false`` - If the user does not exists <br>
    - Delete | `/users/deleteUser` - Deletes a User from the database
        ```json
        {
            "username": "username",
            "password": "password"
        }
        ```
        Returns: <br>
            ``String userDNR`` - User does not exists <br>
            ``String userDeleted`` - User deleted <br>
            ``String WrongPassword`` - Wrong password <br>
    - Get | `/users/IsTutor/{userID}` - Returns true or false if the user is a tutor <br>
 - Admins
    - Get | `/admins` - Returns a JSON of Admins
    - Post | `/admin/createAdmin/{username}` - Creates a Admin with the username (MUST ALREADY BE A USER) <br>
        
        Returns: <br>
            ``404`` - User not found <br>
            ``403`` - Admin already exists<br>
            ``admin`` - A  <br>
    - Delete | `/admin/deleteAdmin/{username}` - Deletes a Admin from the database <br>
        
        Returns: <br>
            ``false`` - Admin not found<br>
            ``true`` - Admin deleted<br>
    - Get | `/admin/getPermissions/{username}` - Displays Permission for a Admin <br>
        
        Returns: <br>
            ``null`` - Admin does not exists<br>
            ``Permissions`` List of Permissions in a String format- <br>
    - Patch | `/admin/updateStatus/{username}` - Updates isActive for Admins <br>
        Returns: <br>
            ``404`` - User not found <br>
            ``403`` - Admin not found<br>
            ``true / false`` - Depending if they are active or not  <br>
