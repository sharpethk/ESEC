"""
Generate Android launcher icons and splash logo from icon.png and splash.png.

- Launcher legacy: ic_launcher.png / ic_launcher_round.png at all densities.
- Adaptive icon foreground: ic_launcher_foreground.png at all densities, with
  the icon scaled into the 66% safe zone (centered, transparent padding).
- Round legacy icon is masked to a circle.
- Splash logo: ic_splash_logo.png placed in drawable-nodpi at 432x432 square,
  preserving aspect ratio with transparent padding.
"""
from __future__ import annotations

from pathlib import Path
from PIL import Image, ImageDraw

ROOT = Path(__file__).resolve().parents[1]
RES  = ROOT / "app" / "src" / "main" / "res"
ICON = ROOT / "icon.png"
SPLASH = ROOT / "splash.png"

# Launcher legacy bitmap sizes (px) per density
LAUNCHER_SIZES = {
    "mdpi":    48,
    "hdpi":    72,
    "xhdpi":   96,
    "xxhdpi":  144,
    "xxxhdpi": 192,
}

# Adaptive icon foreground sizes (108dp -> px). Safe zone is 66dp centered.
FOREGROUND_SIZES = {
    "mdpi":    108,
    "hdpi":    162,
    "xhdpi":   216,
    "xxhdpi":  324,
    "xxxhdpi": 432,
}

SAFE_ZONE_RATIO = 66 / 108  # adaptive icon safe zone


def fit_into_canvas(src: Image.Image, canvas_size: int, content_ratio: float = 1.0) -> Image.Image:
    """Return RGBA image of canvas_size, with src fit (preserving aspect ratio)
    into a centered square of canvas_size * content_ratio, on transparent bg."""
    canvas = Image.new("RGBA", (canvas_size, canvas_size), (0, 0, 0, 0))
    target = max(1, int(canvas_size * content_ratio))
    s = src.copy()
    # preserve aspect ratio: fit longest side to target
    s.thumbnail((target, target), Image.LANCZOS)
    x = (canvas_size - s.width) // 2
    y = (canvas_size - s.height) // 2
    if s.mode != "RGBA":
        s = s.convert("RGBA")
    canvas.paste(s, (x, y), s)
    return canvas


def circle_mask(img: Image.Image) -> Image.Image:
    size = img.size
    mask = Image.new("L", size, 0)
    ImageDraw.Draw(mask).ellipse((0, 0, size[0], size[1]), fill=255)
    out = Image.new("RGBA", size, (0, 0, 0, 0))
    out.paste(img, (0, 0), mask)
    return out


def main() -> None:
    if not ICON.exists():
        raise SystemExit(f"missing: {ICON}")
    if not SPLASH.exists():
        raise SystemExit(f"missing: {SPLASH}")

    icon_src = Image.open(ICON).convert("RGBA")
    splash_src = Image.open(SPLASH).convert("RGBA")

    # 1) Launcher legacy square + round
    for density, size in LAUNCHER_SIZES.items():
        out_dir = RES / f"mipmap-{density}"
        out_dir.mkdir(parents=True, exist_ok=True)

        square = fit_into_canvas(icon_src, size, content_ratio=1.0)
        square.save(out_dir / "ic_launcher.png", "PNG")

        round_img = circle_mask(square)
        round_img.save(out_dir / "ic_launcher_round.png", "PNG")
        print(f"  wrote {out_dir/'ic_launcher.png'} ({size}px)")

    # 2) Adaptive icon foreground PNG (with safe zone padding)
    for density, size in FOREGROUND_SIZES.items():
        out_dir = RES / f"mipmap-{density}"
        out_dir.mkdir(parents=True, exist_ok=True)
        fg = fit_into_canvas(icon_src, size, content_ratio=SAFE_ZONE_RATIO)
        fg.save(out_dir / "ic_launcher_foreground.png", "PNG")
        print(f"  wrote {out_dir/'ic_launcher_foreground.png'} ({size}px)")

    # 3) Splash logo: single nodpi PNG, 432x432 with aspect-preserved fit.
    splash_dir = RES / "drawable-nodpi"
    splash_dir.mkdir(parents=True, exist_ok=True)
    splash_logo = fit_into_canvas(splash_src, 432, content_ratio=1.0)
    splash_logo.save(splash_dir / "ic_splash_logo.png", "PNG")
    print(f"  wrote {splash_dir/'ic_splash_logo.png'} (432px)")

    print("done.")


if __name__ == "__main__":
    main()
