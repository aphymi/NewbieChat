NewbieChat
==========
###This plugin is intended to assist servers where new members need to go through some process with staff.    
###It lets staff pull newbies into separate chat rooms where newbies cannot see main chat, though staff can.

A "newbie" is someone without the 'newbiechat.notnewbie' permission.
They show up on /newbies.

Staff have the 'newbiechat.staffmember' permission and show up on /staff.
Also, staff can see main chat while in a chat room, and see chat from
chat rooms as specified in config.yml

To avoid newbies getting stuck in rooms without anyone able to see their chat, all players are forced out of rooms that don't have any staff in them.
e.g. A staff member in a room with a newbie can use /leave, and both the staff member and newbie would get put back in main chat.

## Commands:
* ncreload
  * Reload config.yml.
  * Permission: newbiechat.reload
  
* newbies
  * Show a list of online newbies.
  * Permission: newbiechat.newbies
  
* staff
  * Show a list of online staff.
  * Permission: newbiechat.staff
  
* chatters
  * Show a list of players that are in each room.
  * Permission: newbiechat.chatters
  
* pullnew
  * Pull the command sender and specified user into an open chat room.
  * Syntax: /pullnew <username>
  * Permission: newbiechat.rooms.pullnew
  
* pull
  * Pull the specified player into the command sender's chat room, or into main chat.
  * Syntax: /pull <username>
  * permission: newbiechat.rooms.pull
  
* join
  * Join the specified chat room.
  * Syntax: /join <room number>
  * permission: newbiechat.rooms.join
  
* leave
  * Leave the current chat room.
  * Permission: newbiechat.rooms.leave

##Permissions
* newbiechat.*
  * Gives access to all NewbieChat permissions.
  
* newbiechat.rooms.*
  * Gives access to all commands involving entering/leaving chat rooms.
  
* newbiechat.notnewbie
  * Tells the plugin that this player is not a newbie.
  
* newbiechat.staffmember
  * Tells the plugin that this player is a member of staff.
  
* newbiechat.reload
  * Allows a player to use /ncreload.
  
* newbiechat.newbies
  * Allows a player to use /newbies.
  
* newbiechat.staff
  * Allows a player to use /staff.
  * Does NOT make a player show up on /staff. Use newbiechat.staffmember for that
  
* newbiechat.chatters
  * Allows a player to use /chatters.
  
* newbiechat.rooms.pullnew
  * Allows a player to use /pullnew.
  
* newbiechat.rooms.pull
  * Allows a player to use /pull.
  
* newbiechat.join
  * Allows a player to use /join.
  
* newbiechat.leave
  * Allows a player to use /leave.
