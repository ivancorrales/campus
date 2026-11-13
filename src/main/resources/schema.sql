CREATE OR REPLACE FUNCTION validar_fecha_nacimiento_estudiante()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.fecha_nacimiento >= CURRENT_DATE THEN
        RAISE EXCEPTION 'La fecha de nacimiento debe ser anterior a hoy'
            USING ERRCODE = 'check_violation';
END IF;

RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_validar_fecha_nacimiento_estudiante ON estudiantes;

CREATE TRIGGER trg_validar_fecha_nacimiento_estudiante
    BEFORE INSERT OR UPDATE ON estudiantes
                         FOR EACH ROW
                         EXECUTE FUNCTION validar_fecha_nacimiento_estudiante();