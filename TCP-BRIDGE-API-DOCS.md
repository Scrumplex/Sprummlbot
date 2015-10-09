## TCP-Api Documentation
### Connection
To connect to it you will need to connect via a TCP client on localhost:port.
Sprummlbot only accepts localhost for security reasons.
### Syntax
#### Command
##### Example
```
command "*arg1*","*arg2*"
```
#### Results
##### Error
```
!e="31",msg="error"
```
So if the line starts with `!e=` there was an error.
##### No Error
```
>output="*result*",msg="ok"
```
If the line starts with `>output=` no error occured
#### Error Codes
The error codes are built of 2 numbers. The example here is `31`. The **first** number is the *category*.
The **second** number is the *error code*.

On our example the *category* is `3(Command Error)`. The *error code* here is `1(Unknown Command)`.
##### Categories
There are 3 types of categories.
| Number 	| Category        	|
|:------:	|-----------------	|
| 1      	| General Error   	|
| 2      	| Teamspeak Error 	|
| 3      	| Command Error   	|
##### General Errors
 - 11: IP not whitelisted

##### Teamspeak Errors
 - 21: No permissions.
 - 22: Lost connection to Teamspeak Server.

##### Command Errors
 - 31: Unknown Command
 - 32: Empty Command
 - 33: Syntax/intnull error (intnull means that a number is not an integer)

### Commands
 - `clientlist`
 - `disc`
 - `kick`
 - `regevents`
 - `sendserv`
 - `sendpriv`
 - `unregevents`

#### `clientlist`
##### Syntax
```
clientlist
```
##### Result
```
>output=["nick="Sprummlbot",cid="3",uid="serveradmin",dbid="1";nick="Test",cid="9",uid="MG10U1dcC6PjWSZnMDhaLtlnyIE=",dbid="9""],msg="ok"
```

more coming soon.