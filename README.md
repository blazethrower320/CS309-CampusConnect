# Backend

### API Calls
 - ## Users
    - GET | ``/users`` - Returns a list of all users
    - GET | ``/users/{id}`` > Returns a User with the specific ID
    - GET | ``/users/findUsername/{username}`` - Returns a List<User> with the username
    - GET | ``/users/findUser/{firstName}`` - Returns a User with the firstName
    - POST | ``/users/login`` - With a Body of
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
    - GET | ``/usernames`` - Returns A list of only Usernames of all Users
    - GET | ``/users/password/{username}`` - Returns the password of the username
    - PATCH | `/users/editUsername` - Edits the username of a user id
        ```json
        {
            "newUsername": "newUsername",
            "newPassword": "newPassword"
        }
        ```

        Returns: <br>
            ``true`` - If it was successfully changed <br>
            ``false`` - If the user does not exists <br>
    - PATCH | `/users/editPassword` - Edits the password of a user id
        ```json
        {
            "newUsername": "newUsername",
            "newPassword": "newPassword"
        }
        ```

        Returns: <br>
            ``true`` - If it was successfully changed <br>
            ``false`` - If the user does not exists <br>
    - DELETE | `/users/deleteUser` - Deletes a User from the database
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
    - GET | `/users/IsTutor/{userID}` - Returns true or false if the user is a tutor <br>
    - GET | `/users/major/{username}` - Returns the major of the given user from the username <br>
    - GET | `/users/editMajor/{username}/{major}` - Updates the major of the user <br>
    - GET | `/users/bio/{username}` - Returns the bio of the given user from the username <br>
    - GET | `/users/editBio/{username}/{bio}` - Updates the bio of the user <br>
    - GET | `/users/classification/{username}` - Returns the classification of the given user from the username <br>
    - GET | `/users/editClassification/{username}/{classification}` - Updates the classification of the user <br>
    - GET | `/users/find/{username}` - Returns a UserRequest with all the user fields + boolean flags for tutor and admin 
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
    - GET | `/admins` - Returns a JSON of Admins <br>
    - POST | `/admin/createAdmin/{username}` - Creates a Admin with the username (MUST ALREADY BE A USER) <br>
        Returns: <br>
            ``404`` - User not found <br>
            ``403`` - Admin already exists<br>
            ``admin`` - Admin Object<br>

    - DELETE | `/admin/deleteAdmin/{username}` - Deletes a Admin from the database <br>
        Returns: <br>
            ``false`` - Admin not found<br>
            ``true`` - Admin deleted<br>

    - GET | `/admin/getPermissions/{username}` - Displays Permission for a Admin <br>
        Returns: <br>
            ``null`` - Admin does not exists<br>
            ``Permissions`` List of Permissions in a String format- <br>

    - PATCH | `/admin/updateStatus/{username}` - Updates isActive for Admins <br>
        Returns: <br>
            ``404`` - User not found <br>
            ``403`` - Admin not found<br>
            ``true / false`` - Depending if they are active or not  <br>
    - POST | `/admin/updateRatingsTutor/{username}/{rating}` - Updates the Rating of a tutor <br>
        Returns: <br>
            ``true`` - Successful <br>
            ``false`` - Failed <br>

 - ## Tutors
    - GET | `/tutors` - Returns a JSON of Tutors <br>
    - POST | `/tutors/createTutor/{username}` - Creates a Tutor with the username (MUST ALREADY BE A USER) <br>
        Returns: <br>
            ``404`` - User not found <br>
            ``403`` - Tutor already exists<br>
            ``tutor`` - Tutor Object<br>

    - DELETE | `/tutors/deleteTutor/{username}` - Deletes a Admin from the database <br>
        Returns: <br>
            ``403`` - Tutor not found<br>
            ``ok`` - Tutor Removed<br>

    - PUT | `/tutors/editTotalClasses` - Edits class count for the user <br>
        Returns: <br>
            ``403`` - Tutor not found <br>
            ``ok`` Tutor updated <br>
    - GET | `/tutors/getTutorRating/{tutorID}` - Returns DOUBLE of the Tutors rating

- ## Sessions
    - GET | `/sessions` - Returns a JSON of Sessions <br>
    - POST | `/sessions/createSession` - Creates a New Session with the Tutor, Also includes them as a Member<br>
        ```json
        {
            "tutorId": 1111
            "className": "Computer Science 309",
            "classCode": "COMS309",
            "meetingLocation": "Pearson",
            "meetingTime": "Friday",
            "dateCreated": "2025-10-29T19:00:00"    
        }
        ```
    - POST | `/sessions/joinSession/{username}/{sessionId}` - Allows a User to join a specific Session<br>
    - GET | `/sessions/getSession/{sessionId}` - Gets a session with the Session ID<br>
        Returns:<br>
            ``404`` - Session Not found<br>
            ``ok`` - Success<br> <br>
    - GET | `/sessions/setMeetingTime` - Sets the Meeting Date of a session<br>
        ```json
        {
            "sessionId": 1,
            "meetingTime": "October 20th, 2025"
        }
        ```
        Returns:<br>
            ``true`` - Successfully changed<br>
            ``false`` - Session was not found<br>
    - GET | `/sessions/getMeetingDate/{sessionId}` - Returns a String of the Meeting Date<br>

- ## Ratings
    - GET | `/ratings` - Returns a List of all Ratings<br>
    - GET | `/ratings/getTutorRatings/{tutorId}` - Returns a List of the Tutors Ratings<br>
    - POST | `/ratings/createRating`<br>
        ```json
        {
            "rating": 5
            "comments": "Very good Tutor Session",
            "tutorID": {
                "id": 1
            },
            "userId":{
                "id": 2
            }
        }
        ```
        Returns:<br>
            ``404`` - Invalid Tutor or User<br>
            ``200`` - Success<br>




