package com.doctordesh.interfaces

import com.doctordesh.models.ChatUsersModel

interface OnDeleteChatListener
{
     fun onDeleteChat(chatUser: ChatUsersModel,pos:Int)

}