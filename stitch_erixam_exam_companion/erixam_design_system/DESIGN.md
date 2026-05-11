---
name: EriXam Design System
colors:
  surface: '#f5faff'
  surface-dim: '#d6dae0'
  surface-bright: '#f5faff'
  surface-container-lowest: '#ffffff'
  surface-container-low: '#eff4fa'
  surface-container: '#eaeef4'
  surface-container-high: '#e4e9ee'
  surface-container-highest: '#dee3e8'
  on-surface: '#171c20'
  on-surface-variant: '#3e4850'
  inverse-surface: '#2c3135'
  inverse-on-surface: '#ecf1f7'
  outline: '#6e7881'
  outline-variant: '#bdc8d1'
  surface-tint: '#00658d'
  primary: '#00658d'
  on-primary: '#ffffff'
  primary-container: '#00aeef'
  on-primary-container: '#003e58'
  inverse-primary: '#82cfff'
  secondary: '#006d2f'
  on-secondary: '#ffffff'
  secondary-container: '#71fa92'
  on-secondary-container: '#007232'
  tertiary: '#c00015'
  on-tertiary: '#ffffff'
  tertiary-container: '#ff7b70'
  on-tertiary-container: '#790009'
  error: '#ba1a1a'
  on-error: '#ffffff'
  error-container: '#ffdad6'
  on-error-container: '#93000a'
  primary-fixed: '#c6e7ff'
  primary-fixed-dim: '#82cfff'
  on-primary-fixed: '#001e2d'
  on-primary-fixed-variant: '#004c6b'
  secondary-fixed: '#74fd94'
  secondary-fixed-dim: '#55e07b'
  on-secondary-fixed: '#002109'
  on-secondary-fixed-variant: '#005322'
  tertiary-fixed: '#ffdad6'
  tertiary-fixed-dim: '#ffb4ac'
  on-tertiary-fixed: '#410002'
  on-tertiary-fixed-variant: '#93000d'
  background: '#f5faff'
  on-background: '#171c20'
  surface-variant: '#dee3e8'
typography:
  display-lg:
    fontFamily: Plus Jakarta Sans
    fontSize: 32px
    fontWeight: '700'
    lineHeight: 40px
  headline-md:
    fontFamily: Plus Jakarta Sans
    fontSize: 24px
    fontWeight: '600'
    lineHeight: 32px
  title-lg:
    fontFamily: Inter
    fontSize: 20px
    fontWeight: '600'
    lineHeight: 28px
  body-lg:
    fontFamily: Inter
    fontSize: 18px
    fontWeight: '400'
    lineHeight: 28px
    letterSpacing: 0.2px
  body-md:
    fontFamily: Inter
    fontSize: 16px
    fontWeight: '400'
    lineHeight: 24px
  label-lg:
    fontFamily: Inter
    fontSize: 14px
    fontWeight: '600'
    lineHeight: 20px
    letterSpacing: 0.1px
  label-sm:
    fontFamily: Inter
    fontSize: 11px
    fontWeight: '500'
    lineHeight: 16px
    letterSpacing: 0.5px
rounded:
  sm: 0.25rem
  DEFAULT: 0.5rem
  md: 0.75rem
  lg: 1rem
  xl: 1.5rem
  full: 9999px
spacing:
  base: 4px
  xs: 4px
  sm: 8px
  md: 16px
  lg: 24px
  xl: 32px
  margin-mobile: 16px
  gutter-mobile: 12px
---

## Brand & Style

The visual identity of this design system is built on **Empowerment, Clarity, and Heritage**. Designed for Eritrean students preparing for critical exams, the UI balances a professional academic tone with a vibrant, motivating energy. 

The design style follows a **Modern Material** approach, heavily influenced by Material Design 3 (M3) principles but refined with a distinct cultural soul. It utilizes high-contrast elements to prevent eye strain during long study sessions and adopts an "Offline-First" aesthetic—meaning the interface feels solid, reliable, and functional even without an active data connection. The interface remains lightweight and fast, prioritizing content (exam questions) over decorative clutter, while subtly weaving in Eritrean national symbols like the olive wreath and geometric patterns into secondary visual layers.

## Colors

This design system uses a primary palette inspired by the Eritrean national identity, optimized for digital readability and psychological motivation.

- **Primary (Eritrean Blue):** Used for key actions, focus states, and branding. It represents professionalism and trust.
- **Secondary (Vibrant Green):** Used for "Success" states, correct answers, and progress completion.
- **Tertiary (Bold Red):** Reserved for "Error" states, incorrect answers, and high-priority alerts.
- **Accent (Golden Yellow):** Dedicated to gamification elements—points, badges, rewards, and streak highlights.
- **Neutral (Crisp White & Soft Grey):** The background is pure white to maximize contrast, while surfaces (cards, input wells) use a soft grey to define boundaries without heavy lines.

## Typography

The typography strategy prioritizes readability and information hierarchy. We use **Plus Jakarta Sans** for headlines to provide a modern, friendly character, and **Inter** for all functional text to ensure maximum legibility across various screen densities.

- **Question Text:** Uses `body-lg` with increased line-height and specific letter-spacing to reduce cognitive load during reading.
- **Exam Titles:** Uses `headline-md` or `title-lg` to provide a clear sense of place.
- **Labels:** Used for metadata like "Subject," "Year," or "Time Remaining."
- **Contrast:** Text colors never fall below a 4.5:1 ratio against their background, ensuring accessibility for all users.

## Layout & Spacing

This design system utilizes a **Fluid Grid** model optimized for Android mobile devices. 

- **Grid:** A 4-column grid for mobile handsets.
- **Margins:** Standard 16px side margins to ensure content does not touch the bezel.
- **Rhythm:** An 8px linear scale (4px, 8px, 16px, 24px, 32px, etc.) governs all padding and margin decisions.
- **Safe Areas:** Adheres to Android system bars and gesture navigation zones, ensuring touch targets remain within the "thumb zone" (the bottom two-thirds of the screen).

## Elevation & Depth

To maintain a lightweight, offline-first feel, this design system avoids heavy, dark shadows. Instead, it uses **Tonal Elevation** and **Subtle Ambient Shadows**.

- **Surface Levels:** The background is at 0dp. Cards and surfaces sit at 1dp or 2dp.
- **Shadows:** Use a very soft, diffused blur (12% opacity of a slightly tinted Blue-Grey) to create a "lifted" effect for actionable cards.
- **Interaction:** On press, cards should use a subtle inner-shadow or a scale-down effect (98%) to provide tactile feedback without relying on complex animations that might lag on lower-end devices.

## Shapes

The shape language is friendly and approachable, utilizing a **Rounded** (16px+) strategy to soften the academic nature of the app.

- **Cards:** Use `rounded-lg` (16px) for a modern, containerized look.
- **Buttons:** Use `rounded-xl` (24px) or full pill-shape for primary actions to make them highly distinct from content cards.
- **Inputs:** Use `rounded-lg` (16px) to match the container language.
- **Icons:** Use rounded terminals (2px stroke width) to ensure visual harmony with the UI components.

## Components

### Buttons
- **Primary:** Solid #00AEEF background with white text. High-contrast and pill-shaped.
- **Secondary:** Outlined with #00AEEF or tinted background. Used for "Previous" or "Skip" actions.
- **Success/Action:** Solid #00AD4F for "Submit" or "Finish Exam."

### Cards
- **Question Cards:** Pure white background, 1px soft grey border, and `rounded-lg` corners. 
- **Subject Cards:** Feature a small, minimalist icon (e.g., a geometric camel for Geography or a wreath for History) in the top right corner.

### Selection Controls
- **Checkboxes & Radios:** Large touch targets (min 48x48dp). Selected states use the Primary Blue with a clear inner-white dot/check.
- **Chips:** Used for filtering years or subjects. Rounded-pill shape with #F5F7FA background.

### Input Fields
- Filled style with a 2px bottom stroke that turns Primary Blue on focus. Labels sit inside the field in a "floating" style per M3 standards.

### Progress & Rewards
- **Progress Bar:** High-visibility #FFCC00 bar against a #F5F7FA track. 
- **Streaks:** A flame icon or "Golden Wreath" symbol to represent daily study consistency.

### Navigation
- **Bottom Navigation:** Fixed at the bottom with labels for Home, Practice, Stats, and Profile. Uses thin-line icons that fill in when active.