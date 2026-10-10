import { DEFENSE_STAGES } from '../../constants/defenseStages.js';

export default function PipelineAnimation() {
    return (
        <ol aria-label="방어 단계">
            {DEFENSE_STAGES.map((stage) => (
                <li key={stage}>{stage}</li>
            ))}
        </ol>
    );
}
