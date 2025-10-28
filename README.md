# Backend

### API Calls
 - ## Users
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
    - Get | `/users/major/{username}` - Returns the major of the given user from the username <br>
    - Get | `/users/editMajor/{username}/{major}` - Updates the major of the user <br>
    - Get | `/users/bio/{username}` - Returns the bio of the given user from the username <br>
    - Get | `/users/editBio/{username}/{bio}` - Updates the bio of the user <br>
    - Get | `/users/classification/{username}` - Returns the classification of the given user from the username <br>
    - Get | `/users/editClassification/{username}/{classification}` - Updates the classification of the user <br>
    - Get | `/users/find/{username}` - Returns a UserRequest with all the user fields + boolean flags for tutor and admin 
        ```json
        {
            "firstName": "firstName",
            "lastName": "lastName",
            "username": "username",
            "password": "password"
            "isTutor" : "isTutor",
            "isAdmin" : "isAdmin",
            "major"   : "major",
            "classification": "classification",
            "bio"     : "bio"
        }
        ```
    - Put | `/users/update` - Updates the User and returns the user.


 - ## Admins
    - Get | `/admins` - Returns a JSON of Admins <br>
    - Post | `/admin/createAdmin/{username}` - Creates a Admin with the username (MUST ALREADY BE A USER) <br>
        Returns: <br>
            ``404`` - User not found <br>
            ``403`` - Admin already exists<br>
            ``admin`` - Admin Object<br>

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
    - Post | `/admin/updateRatingsTutor/{username}/{rating}` - Updates the Rating of a tutor <br>
        Returns: <br>
            ``true`` - Successful <br>
            ``false`` - Failed <br>

 - ## Tutors
    - Get | `/tutors` - Returns a JSON of Tutors <br>
    - Post | `/tutors/createTutor/{username}` - Creates a Tutor with the username (MUST ALREADY BE A USER) <br>
        Returns: <br>
            ``404`` - User not found <br>
            ``403`` - Tutor already exists<br>
            ``tutor`` - Tutor Object<br>

    - Delete | `/tutors/deleteTutor/{username}` - Deletes a Admin from the database <br>
        Returns: <br>
            ``403`` - Tutor not found<br>
            ``ok`` - Tutor Removed<br>

    - PUT | `/tutors/editTotalClasses` - Edits class count for the user <br>
        Returns: <br>
            ``403`` - Tutor not found <br>
            ``ok`` Tutor updated <br>
- ## Sessions
    - Get | `/sessions` - Returns a JSON of Sessions <br>
    - Post | `/sessions/createSession` - Creates a New Session with the Tutor, Also includes them as a Member<br>
        ```json
        {
            "userId": 1111,
            "tutorId": 1111
            "className": "Computer Science 309"
            "classCode": "COMS309"
            "meetingLocation": "Pearson"
            "meetingTime": "Friday"
        }
        ```
        Returns: <br>
            ``400`` - Tutor Not found<br>
            ``ok`` - Success<br>
    - Post | `/sessions/joinSession` - Allows a User to join a specific Session<br>
        ```json
        {
            "sessionId": 1111,
            "userId": 1111
        }
        ```
        Returns:<br>
            ``404`` - Session Not found<br>
            ``ok`` - Success<br>
    - Get | `/sessions/getSession/{sessionId}` - Gets a session with the Session ID<br>
        Returns:<br>
            ``404`` - Session Not found<br>
            ``ok`` - Success<br> <br>
    - Get | `/sessions/setMeetingTime` - Sets the Meeting Date of a session<br>
        ```json
        {
            "sessionId": 1,
            "meetingTime": "October 20th, 2025"
        }
        ```
        Returns:<br>
            ``true`` - Successfully changed<br>
            ``false`` - Session was not found<br>
    - Get | `/sessions/getMeetingDate/{sessionId}` - Returns a String of the Meeting Date<br>

- ## Session Members
    - Get | `/sessions/getAllSessionMembers/{sessionId}` - Gets Session Members<br>
        Returns:<br>
            ``ok`` - Returns List<members><br>
    - Get | `/sessions/getSessionTutor/{sessionId}` - Gets Tutor from Session<br>
        Returns:
            ``400`` - Session Not Found<br>
            ``401`` - Tutor not found<br>
            ``ok`` - Success<br>



