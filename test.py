import discord
from discord.ext import commands

intents = discord.Intents().all()
bot = commands.Bot(command_prefix='!',intents=intents)

@bot.command()
async def status(ctx):
    for s in ctx.author.activities:
        if isinstance(s, discord.CustomActivity):
            await ctx.send(s.name)

bot.run('OTE3ODkxOTkxNjg4MzM5NDU2.Ya_TiA.htrqnJ0ywi6Y6ETka3YjBG2u9mE')