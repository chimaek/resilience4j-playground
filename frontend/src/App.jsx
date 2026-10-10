import ControlPanel from './components/ControlPanel/ControlPanel.jsx';
import ChaosPanel from './components/ChaosPanel/ChaosPanel.jsx';
import MetricsPanel from './components/MetricsPanel/MetricsPanel.jsx';
import CircuitIndicator from './components/CircuitIndicator/CircuitIndicator.jsx';
import LogPanel from './components/LogPanel/LogPanel.jsx';
import ChartPanel from './components/ChartPanel/ChartPanel.jsx';
import PipelineAnimation from './components/PipelineAnimation/PipelineAnimation.jsx';
import ConfigEditor from './components/ConfigEditor/ConfigEditor.jsx';

export default function App() {
    return (
        <div className="app">
            <header>
                <h1>Resilience4j Playground</h1>
                <CircuitIndicator />
            </header>
            <main>
                <aside>
                    <ControlPanel />
                    <ChaosPanel />
                    <MetricsPanel />
                    <ConfigEditor />
                </aside>
                <section>
                    <PipelineAnimation />
                    <ChartPanel />
                    <LogPanel />
                </section>
            </main>
        </div>
    );
}
