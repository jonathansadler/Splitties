/*
 * Copyright (c) 2018. Louis Cognault Ayeva Derman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package xyz.louiscad.splittiessample.demo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import splitties.checkedlazy.uiLazy
import splitties.snackbar.action
import splitties.snackbar.onDismiss
import splitties.snackbar.snack
import splitties.snackbar.snackForever
import splitties.viewdsl.core.setContentView
import splitties.views.appcompat.configActionBar
import splitties.views.appcompat.showHomeAsUp
import splitties.views.onClick
import xyz.louiscad.splittiessample.R
import java.lang.Integer.MAX_VALUE

class DemoActivity : AppCompatActivity(), DemoAdapter.DemoViewHolder.Host {

    private val ui by uiLazy { DemoUi(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(ui)
        configActionBar {
            showHomeAsUp = true
        }
        ui.recyclerView.let {
            it.setHasFixedSize(true)
            it.layoutManager = LinearLayoutManager(this)
            it.adapter = DemoAdapter(this)
        }
        ui.fab.onClick {
            ui.root.snack(R.string.title_feature_not_available) {
                onDismiss {
                    ui.root.snackForever(R.string.msg_go_to_pc_manually)
                }
            }
        }
    }

    override fun onDemoItemClicked(demoItem: DemoItem) {
        ui.root.snackForever(R.string.msg_marketing_guy_invents_new_feature) {
            action(R.string.scroll_to_the_end) {
                ui.recyclerView.scrollToPosition(MAX_VALUE - 1)
            }
        }
    }
}