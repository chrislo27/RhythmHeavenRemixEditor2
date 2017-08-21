package io.github.chrislo27.rhre3.entity.model.cue

import com.badlogic.gdx.math.Rectangle
import io.github.chrislo27.rhre3.entity.model.MultipartEntity
import io.github.chrislo27.rhre3.registry.GameRegistry
import io.github.chrislo27.rhre3.registry.datamodel.impl.CuePointer
import io.github.chrislo27.rhre3.registry.datamodel.impl.RandomCue
import io.github.chrislo27.rhre3.track.Remix
import io.github.chrislo27.toolboks.util.gdxutils.random


class RandomCueEntity(remix: Remix, datamodel: RandomCue) : MultipartEntity<RandomCue>(remix, datamodel) {

    init {
        bounds.width = datamodel.cues.map(CuePointer::duration).max() ?:
                error("Datamodel ${datamodel.id} has no internal cues")
    }

    private fun reroll() {
        internal.clear()
        internal +=
                datamodel.cues.map { GameRegistry.data.objectMap[it.id] }.random()?.createEntity(remix)?.apply {
                    if (this !is CueEntity) {
                        error("Entity ${this.datamodel.id} created for random cue ${this@RandomCueEntity.datamodel.id} isn't a cue entity")
                    }

                    this.bounds.x = this@RandomCueEntity.bounds.x
                    this.bounds.width = this@RandomCueEntity.bounds.width
                    this.bounds.y = this@RandomCueEntity.bounds.y
                } ?: error("Null returned on randomization")
    }

    override fun updateInternalCache(oldBounds: Rectangle) {
        translateInternal(oldBounds)
        if (internal.isEmpty())
            reroll()
    }

    override fun onStart() {
        reroll()
        super.onStart()
    }

    override fun copy(remix: Remix): RandomCueEntity {
        return RandomCueEntity(remix, datamodel).also {
            it.bounds.set(this.bounds)
            it.semitone = this.semitone
        }
    }
}