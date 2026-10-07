# GSK-64: repeat the reporter's ATLAS steps (open cohort #1, Generation tab, Generate on EUNOMIA, read People / Records)
# with a full-screen ffmpeg recording and screenshots. Usage: OUT=dir TAG=before python3 atlas_generate.py
import asyncio, subprocess, os
from playwright.async_api import async_playwright
OUT=os.environ.get("OUT"); TAG=os.environ.get("TAG","run"); DISPLAY=os.environ.get("DISPLAY",":0")
os.makedirs(OUT,exist_ok=True)
def shot(name): subprocess.run(["import","-display",DISPLAY,"-window","root",f"{OUT}/{TAG}_{name}.png"],check=True); print("shot",name,flush=True)
async def main():
    rec=subprocess.Popen(["ffmpeg","-y","-loglevel","error","-f","x11grab","-video_size","1600x1000","-framerate","15","-i",DISPLAY,"-c:v","libx264","-preset","veryfast","-pix_fmt","yuv420p",f"{OUT}/{TAG}_atlas_generation.mp4"],stdin=subprocess.PIPE)
    async with async_playwright() as p:
        b=await p.chromium.launch(headless=False,args=["--window-position=0,0","--window-size=1600,1000","--no-first-run","--disable-infobars","--test-type"])
        pg=await (await b.new_context(no_viewport=True)).new_page()
        pg.on("dialog",lambda d: asyncio.ensure_future(d.accept()))
        try:
            await pg.goto("http://localhost:8090/atlas/#/cohortdefinitions"); await pg.wait_for_timeout(4000)
            await pg.locator("text=Demo new users of diclofenac").first.wait_for(timeout=60000); await pg.wait_for_timeout(1500)
            await pg.click("text=Demo new users of diclofenac"); await pg.locator("text=Cohort Entry Events >> visible=true").first.wait_for(timeout=60000); await pg.wait_for_timeout(2500)
            shot("01_definition")
            await pg.click("a:has-text('Generation')"); await pg.wait_for_selector("button:has-text('Generate')"); await pg.wait_for_timeout(1500)
            await pg.click("button:has-text('Generate')"); await pg.wait_for_timeout(2000); shot("02_generating")
            for i in range(40):
                await pg.wait_for_timeout(3000)
                if await pg.locator("td:has-text('COMPLETE')").count(): break
                if i in (10,20,30): await pg.reload(); await pg.wait_for_timeout(4000); await pg.click("a:has-text('Generation')")
            await pg.reload(); await pg.wait_for_timeout(5000)
            await pg.click("a:has-text('Generation')"); await pg.wait_for_timeout(3000)
            row=pg.locator("tr:has-text('Eunomia')").first
            print("ROW:", (await row.inner_text()).replace("\n"," | "),flush=True)
            await row.scroll_into_view_if_needed(); await pg.wait_for_timeout(2000); shot("03_generation_complete")
        finally:
            await pg.wait_for_timeout(1500); rec.communicate(b"q"); await b.close()
asyncio.run(main())
