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
