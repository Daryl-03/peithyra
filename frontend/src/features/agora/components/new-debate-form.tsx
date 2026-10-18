"use client";

import { zodResolver } from "@hookform/resolvers/zod";
import { ArrowRight, LoaderCircle } from "lucide-react";
import { useAction } from "next-safe-action/hooks";
import { useState } from "react";
import { Controller, useForm } from "react-hook-form";
import type z from "zod";
import { Button } from "@/components/ui/button";
import {
    Field,
    FieldError,
    FieldGroup,
    FieldLabel,
} from "@/components/ui/field";
import { Input } from "@/components/ui/input";
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select";
import { Textarea } from "@/components/ui/textarea";
import { applyServerErrors } from "@/lib/forms/apply-server-errors";
import { createDebate } from "../actions/create-debate";
import {
    createDebateFormSchema,
    defaultCreateDebateValues,
} from "../models/create-debate-schema";
import { DebateSide } from "../models/debate-side";

export function NewDebateForm({ onSuccess }: { onSuccess?: () => void }) {
    const [customTurnTime, setCustomTurnTime] = useState(
        ![60, 180, 600].includes(
            defaultCreateDebateValues.turnTimeLimitSeconds,
        ),
    );
    const form = useForm<z.infer<typeof createDebateFormSchema>>({
        resolver: zodResolver(createDebateFormSchema),
        defaultValues: defaultCreateDebateValues,
        mode: "onBlur",
    });

    const { executeAsync, isPending } = useAction(createDebate, {
        onSuccess: () => {
            onSuccess?.();
        },
        onError: ({ error }) => {
            applyServerErrors(
                form.setError,
                createDebateFormSchema.keyof().options,
                error,
            );
        },
    });
    const pending = form.formState.isSubmitting || isPending;

    function onSubmit(data: z.infer<typeof createDebateFormSchema>) {
        form.clearErrors("root");
        executeAsync(data);
    }

    return (
        <form
            className="space-y-4"
            onSubmit={form.handleSubmit(onSubmit)}
            aria-busy={pending}
        >
            <FieldGroup className="space-y">
                <Controller
                    name="proposition"
                    control={form.control}
                    render={({ field, fieldState }) => (
                        <Field data-invalid={fieldState.invalid}>
                            <FieldLabel htmlFor="proposition">
                                Proposition
                            </FieldLabel>
                            <Input
                                {...field}
                                id="proposition"
                                aria-invalid={fieldState.invalid}
                                aria-describedby={
                                    fieldState.invalid
                                        ? "proposition-error"
                                        : undefined
                                }
                                readOnly={pending}
                                placeholder="ex. La France devrait interdire les voitures thermiques."
                                autoComplete="off"
                                required
                            />
                            {fieldState.invalid && (
                                <FieldError
                                    id="proposition-error"
                                    errors={[fieldState.error]}
                                />
                            )}
                        </Field>
                    )}
                />

                <Controller
                    name="description"
                    control={form.control}
                    render={({ field, fieldState }) => (
                        <Field data-invalid={fieldState.invalid}>
                            <FieldLabel htmlFor="description">
                                Description
                            </FieldLabel>
                            <Textarea
                                {...field}
                                id="description"
                                aria-invalid={fieldState.invalid}
                                aria-describedby={
                                    fieldState.invalid
                                        ? "description-error"
                                        : undefined
                                }
                                readOnly={pending}
                                placeholder="ex. Une description détaillée de la proposition."
                                autoComplete="off"
                                required
                            />
                            {fieldState.invalid && (
                                <FieldError
                                    id="description-error"
                                    errors={[fieldState.error]}
                                />
                            )}
                        </Field>
                    )}
                />
                <Controller
                    name="side"
                    control={form.control}
                    render={({ field, fieldState }) => (
                        <Field data-invalid={fieldState.invalid}>
                            <FieldLabel htmlFor="side">Votre camp</FieldLabel>
                            <Select
                                name={field.name}
                                value={field.value}
                                onValueChange={(value) => {
                                    if (value !== null) field.onChange(value);
                                }}
                                items={[
                                    { value: DebateSide.For, label: "Pour" },
                                    {
                                        value: DebateSide.Against,
                                        label: "Contre",
                                    },
                                ]}
                                disabled={pending}
                                required
                            >
                                <SelectTrigger
                                    id="side"
                                    ref={field.ref}
                                    onBlur={field.onBlur}
                                    className="w-full bg-card px-4 text-base data-[size=default]:h-12"
                                    aria-invalid={fieldState.invalid}
                                    aria-describedby={
                                        fieldState.invalid
                                            ? "side-error"
                                            : undefined
                                    }
                                >
                                    <SelectValue />
                                </SelectTrigger>
                                <SelectContent>
                                    <SelectItem value={DebateSide.For}>
                                        Pour
                                    </SelectItem>
                                    <SelectItem value={DebateSide.Against}>
                                        Contre
                                    </SelectItem>
                                </SelectContent>
                            </Select>
                            {fieldState.invalid && (
                                <FieldError
                                    id="side-error"
                                    errors={[fieldState.error]}
                                />
                            )}
                        </Field>
                    )}
                />
                <Controller
                    name="numberOfTurns"
                    control={form.control}
                    render={({ field, fieldState }) => (
                        <Field data-invalid={fieldState.invalid}>
                            <FieldLabel htmlFor="numberOfTurns">
                                Nombre de tours
                            </FieldLabel>
                            <Input
                                {...field}
                                id="numberOfTurns"
                                type="number"
                                min={1}
                                max={10}
                                step={1}
                                value={
                                    Number.isNaN(field.value) ? "" : field.value
                                }
                                onChange={(event) =>
                                    field.onChange(event.target.valueAsNumber)
                                }
                                aria-invalid={fieldState.invalid}
                                aria-describedby={
                                    fieldState.invalid
                                        ? "numberOfTurns-error"
                                        : undefined
                                }
                                readOnly={pending}
                                required
                            />
                            {fieldState.invalid && (
                                <FieldError
                                    id="numberOfTurns-error"
                                    errors={[fieldState.error]}
                                />
                            )}
                        </Field>
                    )}
                />
                <Controller
                    name="turnTimeLimitSeconds"
                    control={form.control}
                    render={({ field, fieldState }) => (
                        <Field data-invalid={fieldState.invalid}>
                            <FieldLabel htmlFor="turnTimePreset">
                                Temps par tour
                            </FieldLabel>
                            <Select
                                value={
                                    customTurnTime
                                        ? "custom"
                                        : String(field.value)
                                }
                                onValueChange={(value) => {
                                    if (value === null) return;
                                    const custom = value === "custom";
                                    setCustomTurnTime(custom);
                                    if (!custom) field.onChange(Number(value));
                                }}
                                items={[
                                    { value: "60", label: "1 minute" },
                                    { value: "180", label: "3 minutes" },
                                    { value: "600", label: "10 minutes" },
                                    { value: "custom", label: "Personnalisé" },
                                ]}
                                disabled={pending}
                            >
                                <SelectTrigger
                                    id="turnTimePreset"
                                    ref={customTurnTime ? undefined : field.ref}
                                    onBlur={field.onBlur}
                                    className="w-full bg-card px-4 text-base data-[size=default]:h-12"
                                    aria-invalid={fieldState.invalid}
                                    aria-describedby={
                                        fieldState.invalid
                                            ? "turnTimeLimitSeconds-error"
                                            : undefined
                                    }
                                >
                                    <SelectValue />
                                </SelectTrigger>
                                <SelectContent>
                                    <SelectItem value="60">1 minute</SelectItem>
                                    <SelectItem value="180">
                                        3 minutes
                                    </SelectItem>
                                    <SelectItem value="600">
                                        10 minutes
                                    </SelectItem>
                                    <SelectItem value="custom">
                                        Personnalisé
                                    </SelectItem>
                                </SelectContent>
                            </Select>
                            {customTurnTime && (
                                <>
                                    <FieldLabel htmlFor="turnTimeLimitSeconds">
                                        Durée personnalisée (secondes)
                                    </FieldLabel>
                                    <Input
                                        {...field}
                                        id="turnTimeLimitSeconds"
                                        type="number"
                                        min={60}
                                        max={600}
                                        step={1}
                                        value={
                                            Number.isNaN(field.value)
                                                ? ""
                                                : field.value
                                        }
                                        onChange={(event) =>
                                            field.onChange(
                                                event.target.valueAsNumber,
                                            )
                                        }
                                        aria-invalid={fieldState.invalid}
                                        aria-describedby={
                                            fieldState.invalid
                                                ? "turnTimeLimitSeconds-error"
                                                : undefined
                                        }
                                        readOnly={pending}
                                        required
                                    />
                                </>
                            )}
                            {fieldState.invalid && (
                                <FieldError
                                    id="turnTimeLimitSeconds-error"
                                    errors={[fieldState.error]}
                                />
                            )}
                        </Field>
                    )}
                />
            </FieldGroup>

            <Button
                type="submit"
                size="lg"
                disabled={pending}
                className="h-12 w-full"
            >
                {pending ? (
                    <>
                        <LoaderCircle
                            aria-hidden="true"
                            className="animate-spin"
                        />
                        Création du débat…
                    </>
                ) : (
                    <>
                        Lancer le débat
                        <ArrowRight aria-hidden="true" />
                    </>
                )}
            </Button>
        </form>
    );
}
