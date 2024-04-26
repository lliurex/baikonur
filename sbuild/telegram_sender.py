#!/usr/bin/env python3
import telegram
import sys
import glob
import pathlib
import os
import asyncio

class TelegramSender:
    def __init__(self) -> None:
        with open('/run/secrets/telegram-token','r') as fd:
            self.bot = telegram.Bot(token=fd.readline().strip())

        with open('/run/secrets/telegram-chat-id','r') as fd:
            self.chat_id = fd.readline().strip()

    def send_message( self, state, package, build_file='' ):
        if state == 'info':
            asyncio.run(self.bot.sendMessage(chat_id=self.chat_id, text='\U0001F3C1 '+ package + " aceptado" ))
            return
        elif state == "exists":
            asyncio.run(self.bot.sendMessage(chat_id=self.chat_id, text = '\U0001F198 '+ package +' ya existe'))
            return
        elif state == "chroot":
            asyncio.run(self.bot.sendMessage(chat_id=self.chat_id, text = '\U00002753 '+ package +' Distro equivocada'))
            return
        elif state == "true" :
            estado = '\U00002705'
        else:
            estado = '\U0000274C'
        log_file = glob.glob( build_file+"/*.build" )[0]
        asyncio.run(self.bot.sendDocument( self.chat_id, open( log_file, 'rb' ), caption=package + ":" + estado ))
