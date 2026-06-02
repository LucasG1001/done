import { useEffect, useCallback } from 'react'
import styles from './InfoGuide.module.css'

interface InfoGuideProps {
  onClose: () => void
}

export function InfoGuide({ onClose }: InfoGuideProps) {
  const handleKeyDown = useCallback((e: KeyboardEvent) => {
    if (e.key === 'Escape') {
      onClose()
    }
  }, [onClose])

  useEffect(() => {
    document.addEventListener('keydown', handleKeyDown)
    return () => document.removeEventListener('keydown', handleKeyDown)
  }, [handleKeyDown])

  return (
    <>
      <div
        className={styles.backdrop}
        onClick={onClose}
        aria-hidden="true"
      />
      <div
        className={styles.modal}
        role="dialog"
        aria-label="Como funciona"
      >
        <div className={styles.header}>
          <h2 className={styles.title}>Como funciona</h2>
          <button
            className={styles.closeButton}
            onClick={onClose}
            aria-label="Fechar guia"
          >
            ×
          </button>
        </div>

        <div className={styles.body}>
          <section className={styles.section}>
            <h3 className={styles.sectionTitle}>
              <span className={styles.sectionIcon}>🔥</span>
              Sequência (Streak)
            </h3>
            <p className={styles.text}>
              A <strong>sequência</strong> conta quantos dias seguidos você completou o hábito
              nos dias programados. Dias em que o hábito não está programado são
              ignorados — eles não quebram nem aumentam sua sequência.
            </p>
            <div className={styles.tipBox}>
              <p className={styles.text}>
                💡 Se hoje é um dia programado e você ainda não completou, a sequência
                mostra o valor dos dias anteriores. Complete para vê-la aumentar!
              </p>
            </div>
          </section>

          <div className={styles.divider} />

          <section className={styles.section}>
            <h3 className={styles.sectionTitle}>
              <span className={styles.sectionIcon}>💔</span>
              Perda de Sequência
            </h3>
            <p className={styles.text}>
              Sua sequência <span className={styles.dangerHighlight}>zera</span> quando
              você deixa de completar o hábito em um dia programado. Por exemplo, se
              seu hábito é toda segunda e quarta e você não completa na segunda, ao
              chegar terça sua sequência volta a <strong>0</strong>.
            </p>
            <div className={styles.warningBox}>
              <p className={styles.text}>
                ⚠️ <strong>Atenção:</strong> se você perder{' '}
                <span className={styles.dangerHighlight}>2 dias programados consecutivos</span>,
                além de zerar a sequência, seu nível <strong>desce em 1</strong>. Fique
                atento para não perder seu progresso!
              </p>
            </div>
          </section>

          <div className={styles.divider} />

          <section className={styles.section}>
            <h3 className={styles.sectionTitle}>
              <span className={styles.sectionIcon}>📈</span>
              Sistema de Níveis
            </h3>
            <p className={styles.text}>
              Seu nível é baseado na <strong>maior sequência já alcançada</strong>. A cada{' '}
              <span className={styles.highlight}>30 dias</span> de recorde na sequência,
              você sobe de nível. O nível mínimo é sempre <strong>1</strong>.
            </p>
          </section>

          <div className={styles.divider} />

          <section className={styles.section}>
            <h3 className={styles.sectionTitle}>
              <span className={styles.sectionIcon}>🏆</span>
              Evolução dos Níveis
            </h3>
            <ul className={styles.levelList}>
              <li className={styles.levelItem}>
                <span className={styles.levelEmoji}>🌱</span>
                <span className={styles.levelLabel}>Nível 1 — Broto</span>
              </li>
              <li className={styles.levelItem}>
                <span className={styles.levelEmoji}>🌿</span>
                <span className={styles.levelLabel}>Nível 2 — Muda</span>
              </li>
              <li className={styles.levelItem}>
                <span className={styles.levelEmoji}>🪴</span>
                <span className={styles.levelLabel}>Nível 3 — Planta</span>
              </li>
              <li className={styles.levelItem}>
                <span className={styles.levelEmoji}>🌳</span>
                <span className={styles.levelLabel}>Nível 4 — Árvore</span>
              </li>
              <li className={styles.levelItem}>
                <span className={styles.levelEmoji}>⭐</span>
                <span className={styles.levelLabel}>Nível 5 — Estrela</span>
              </li>
              <li className={styles.levelItem}>
                <span className={styles.levelEmoji}>🔥</span>
                <span className={styles.levelLabel}>Nível 6 — Chama</span>
              </li>
              <li className={styles.levelItem}>
                <span className={styles.levelEmoji}>💎</span>
                <span className={styles.levelLabel}>Nível 7 — Diamante</span>
              </li>
              <li className={styles.levelItem}>
                <span className={styles.levelEmoji}>👑</span>
                <span className={styles.levelLabel}>Nível 8+ — Lenda</span>
              </li>
            </ul>
          </section>

          <div className={styles.divider} />

          <section className={styles.section}>
            <h3 className={styles.sectionTitle}>
              <span className={styles.sectionIcon}>📱</span>
              Dados e Armazenamento
            </h3>
            <p className={styles.text}>
              Todos os seus dados são salvos <strong>localmente no navegador</strong> (localStorage).
              Nada é enviado para servidores. Se você limpar os dados do navegador ou
              trocar de dispositivo, seus hábitos serão perdidos.
            </p>
            <div className={styles.tipBox}>
              <p className={styles.text}>
                💡 Use sempre o mesmo navegador para manter seu progresso.
              </p>
            </div>
          </section>
        </div>
      </div>
    </>
  )
}
