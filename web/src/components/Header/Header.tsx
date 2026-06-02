import styles from './Header.module.css'

interface HeaderProps {
  onInfoClick: () => void
}

export function Header({ onInfoClick }: HeaderProps) {
  return (
    <header className={styles.container}>
      <h1 className={styles.title}>done</h1>
      <button
        className={styles.infoButton}
        onClick={onInfoClick}
        aria-label="Como funciona"
      >
        ?
      </button>
    </header>
  )
}
