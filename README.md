# status-supporter-discord-bot

## what this bot doing and how it can help your server grow
## [test/support server](https://discord.gg/9gWBUpvfvj)

Basically, this bot is rewarding members for having a certain custom user status by giving them certain roles.

This will help the server grow by making your members show your server to their friends.

## directory
1. [How it works as a server member](#how-it-works-as-a-server-member)
2. [How to set it up as an Admin](#how-to-set-it-up-as-an-admin)
3. [Every Command](#every-command)
4. [How to host the bot](#how-to-host-the-bot)


## How it works as a server member

Do `/info` and set the support status, you can see there as your custom status. Now do `/approve_status` Now take a look at the roles you can get. Which you can also see with the `/info` command.
Some of them may require you to have a streak of a certato showmount of days to get the role.


## How to set it up as an Admin

**First of all, you have to make sure that the bot role is higher than every other role, so it can manage the roles.**

Now you need to find an interesting status supporter message, the members can set as their status. Make sure it's not longer than 128 characters!
Now do `/set_server_status status:status supporter message` and set the status supporter message as parameter.

Now do `/role action: add role: role`, you can also add the optional parameter "days:" to only give this role to members who had a custom status since this amount of days.
After you change the support status, the status supporter has 48 hours to change their user status. Otherwise, they will lose their status supporter roles.



## Every Command

### 1. approve_status:
take a look [here](#how-it-works-as-a-server-member)

### 2. info:
Shows server information: Amount of status supporter, current support status, and the status support roles you can get as a reward.

### 3. set server status:
Lets you set the server status of the server, which the users can put in their custom status to support the server and to get roles as reward.

### 4. role:

1. actions:
- add:lets you add a role to the List of roles, members can get as reward.
- edit:lets you edit the amount of days a member requires to be a status supporter to get this role
- remove: deletes a role from the status supporter roles List

2. role: The role you would like to add/edit/remove from the status supporter roles List

3. Days: the amount of days a member requires to be a status supporter to get this role.

### 5. my_streak:
shows your streak of days of being a status supporter for this server.


## How to host the bot:

Contact me on discord if you need help! ( `Carl ;)#2284` )

Every server needs it's own version of this bot because it has to make lots of requests to constatly check the members status. Otherwise the ip address
Would get rate limited and banned.
**That's why it's very important that this bot is not sharing its ip address with any other discord bot!**

**when creating the bot at the developer portal, make sure to enable both Privileged Gateway Intents!**

run this [StatusSupporterBot.jar](https://github.com/Carl-Br/status-supporter-discord-bot/raw/main/Bot/grow/out/artifacts/grow_jar/StatusSupporterBot.jar) file on your server and make sure to add a `config.json` file in the same directory, which has to look like this:
```
{
  "token":"",
  "embedColor":"25,255,25",
  "guildId": "",
  "maxReqPerSecondStatusChecker": "10"
}
```
- token: token of the bot,
- embedColor: the color of the embed, the Bot sends as a response
- guildId: the guildId of the server you want to user this bot on.
- maxReqPerSecondStatusChecker: The amount of requests he bot makes to check a members status per second ( must be between 0 and 14)
      
